from fastapi import APIRouter, HTTPException, Depends, Header
from pydantic import BaseModel
import secrets
import time
from typing import Optional

router = APIRouter(prefix="/api/auth", tags=["SOC Authentication"])

# Active sessions store in memory
ACTIVE_SESSIONS = {}

# Authorized SOC operators
AUTHORIZED_OPERATORS = {
    "admin": {
        "password": "vex_soc_2026!",
        "role": "CHIEF_SOC_OPERATOR",
        "display_name": "SecOps Commander (admin)"
    },
    "operator": {
        "password": "soc_guard!",
        "role": "TIER_2_ANALYST",
        "display_name": "SOC Analyst (operator)"
    }
}

class LoginRequest(BaseModel):
    username: str
    password: str

class LoginResponse(BaseModel):
    success: bool
    token: str
    operator: str
    role: str
    expires_in: int

class VerifyResponse(BaseModel):
    valid: bool
    operator: Optional[str] = None
    role: Optional[str] = None

@router.post("/login", response_model=LoginResponse)
def soc_login(payload: LoginRequest):
    user = payload.username.strip()
    pwd = payload.password.strip()

    # Support admin/vex_soc_2026! or default admin/admin fallback
    account = AUTHORIZED_OPERATORS.get(user)
    is_valid = False
    if account and account["password"] == pwd:
        is_valid = True
    elif user == "admin" and pwd == "admin":
        is_valid = True
        account = AUTHORIZED_OPERATORS["admin"]

    if not is_valid:
        raise HTTPException(
            status_code=401,
            detail="Invalid Operator Credentials. Access Denied by VEX Security Policy."
        )

    # Generate cryptographically secure session token
    token = f"vex_soc_{secrets.token_hex(24)}"
    ACTIVE_SESSIONS[token] = {
        "user": user,
        "role": account["role"],
        "display_name": account["display_name"],
        "created_at": time.time(),
        "expires_at": time.time() + 86400  # 24 hours
    }

    return LoginResponse(
        success=True,
        token=token,
        operator=account["display_name"],
        role=account["role"],
        expires_in=86400
    )

@router.get("/verify", response_model=VerifyResponse)
def verify_session(authorization: Optional[str] = Header(None), token: Optional[str] = None):
    auth_token = token
    if not auth_token and authorization:
        parts = authorization.split(" ")
        if len(parts) == 2 and parts[0].lower() == "bearer":
            auth_token = parts[1]
        else:
            auth_token = authorization

    if not auth_token or auth_token not in ACTIVE_SESSIONS:
        return VerifyResponse(valid=False)

    session = ACTIVE_SESSIONS[auth_token]
    if time.time() > session["expires_at"]:
        del ACTIVE_SESSIONS[auth_token]
        return VerifyResponse(valid=False)

    return VerifyResponse(
        valid=True,
        operator=session["display_name"],
        role=session["role"]
    )

@router.post("/logout")
def soc_logout(authorization: Optional[str] = Header(None), token: Optional[str] = None):
    auth_token = token
    if not auth_token and authorization:
        parts = authorization.split(" ")
        if len(parts) == 2 and parts[0].lower() == "bearer":
            auth_token = parts[1]
        else:
            auth_token = authorization

    if auth_token and auth_token in ACTIVE_SESSIONS:
        del ACTIVE_SESSIONS[auth_token]

    return {"success": True, "message": "Operator session terminated."}
