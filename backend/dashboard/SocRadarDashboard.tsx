"use client";

import React, { useState, useEffect, useRef } from "react";

// Types
export interface ThreatEvent {
  event_id: string;
  device_id: string;
  platform: string;
  threat_type: string;
  risk_score: number;
  risk_level: "SAFE" | "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  action: "LOG" | "MONITOR" | "WARN" | "BLOCK" | "QUARANTINE" | "DECEIVE_CANARY";
  explanation: string;
  recommendation: string;
  mitre_attack?: {
    technique_id: string;
    technique_name: string;
    tactic: string;
  };
  timestamp: number;
}

export interface BlockedTarget {
  target: string;
  target_type: string;
  threat_type: string;
  risk_level: string;
  source_platform: string;
  reason: string;
  timestamp: number;
  hit_count: number;
}

export default function SocRadarDashboard() {
  const [theme, setTheme] = useState<"dark" | "light">("dark");
  const [events, setEvents] = useState<ThreatEvent[]>([]);
  const [blocklist, setBlocklist] = useState<BlockedTarget[]>([]);
  const [wsConnected, setWsConnected] = useState(false);
  const [activeSubscribers, setActiveSubscribers] = useState(0);
  const [newTargetInput, setNewTargetInput] = useState("");
  const [selectedThreat, setSelectedThreat] = useState<ThreatEvent | null>(null);
  const [radarThreats, setRadarThreats] = useState<Array<{ angle: number; distance: number; label: string; level: string }>>([]);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const wsRef = useRef<WebSocket | null>(null);

  // Initialize Data & WebSocket Connection
  useEffect(() => {
    // 1. Fetch initial events and blocklist
    fetch("/events/")
      .then((res) => res.json())
      .then((data) => {
        if (Array.isArray(data)) {
          setEvents(data);
          // Map to radar coordinates
          const mapped = data.slice(0, 8).map((evt: ThreatEvent, idx: number) => ({
            angle: (idx * 45) * (Math.PI / 180),
            distance: 0.3 + (evt.risk_score / 150),
            label: evt.threat_type,
            level: evt.risk_level
          }));
          setRadarThreats(mapped);
        }
      })
      .catch((err) => console.error("Error fetching events:", err));

    fetch("/api/blocklist/list")
      .then((res) => res.json())
      .then((data) => {
        if (data.threats) setBlocklist(data.threats);
        if (data.active_subscribers !== undefined) setActiveSubscribers(data.active_subscribers);
      })
      .catch((err) => console.error("Error fetching blocklist:", err));

    // 2. Establish Real-time WebSocket connection to Blast Radius Mesh
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const wsUrl = `${protocol}//${window.location.host}/api/blocklist/ws`;
    const socket = new WebSocket(wsUrl);

    socket.onopen = () => {
      setWsConnected(true);
      console.log("[VEX SOC] Real-time threat broadcast mesh connected.");
    };

    socket.onmessage = (event) => {
      try {
        const payload = JSON.parse(event.data);
        if (payload.event === "BLAST_RADIUS_CONTAINMENT_EVENT") {
          const item = payload.data;
          setBlocklist((prev) => [
            {
              target: item.target,
              target_type: item.target_type || "DOMAIN",
              threat_type: item.threat_type || "UNKNOWN",
              risk_level: item.risk_level || "CRITICAL",
              source_platform: item.source_platform || "UNIVERSAL",
              reason: item.reason || "Real-time auto containment",
              timestamp: item.contained_at || Date.now(),
              hit_count: 1
            },
            ...prev
          ]);
        }
      } catch (err) {
        console.error("WS parse error", err);
      }
    };

    socket.onclose = () => setWsConnected(false);
    wsRef.current = socket;

    return () => {
      socket.close();
    };
  }, []);

  // Canvas Radar Sweep Animation
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    let sweepAngle = 0;
    let animationFrameId: number;

    const render = () => {
      const w = canvas.width;
      const h = canvas.height;
      const cx = w / 2;
      const cy = h / 2;
      const radius = Math.min(cx, cy) - 10;

      // Clear dark background
      ctx.fillStyle = "#050b14";
      ctx.fillRect(0, 0, w, h);

      // Draw concentric radar rings
      ctx.strokeStyle = "rgba(0, 255, 170, 0.25)";
      ctx.lineWidth = 1.5;
      for (let r = 1; r <= 4; r++) {
        ctx.beginPath();
        ctx.arc(cx, cy, (radius / 4) * r, 0, Math.PI * 2);
        ctx.stroke();
      }

      // Draw crosshairs
      ctx.beginPath();
      ctx.moveTo(cx - radius, cy);
      ctx.lineTo(cx + radius, cy);
      ctx.moveTo(cx, cy - radius);
      ctx.lineTo(cx, cy + radius);
      ctx.stroke();

      // Draw rotating sweep sector
      sweepAngle += 0.035;
      if (sweepAngle > Math.PI * 2) sweepAngle = 0;

      const gradient = ctx.createConicGradient(sweepAngle, cx, cy);
      gradient.addColorStop(0, "rgba(0, 255, 170, 0.4)");
      gradient.addColorStop(0.15, "rgba(0, 255, 170, 0.0)");
      gradient.addColorStop(1, "rgba(0, 255, 170, 0.0)");

      ctx.fillStyle = gradient;
      ctx.beginPath();
      ctx.arc(cx, cy, radius, 0, Math.PI * 2);
      ctx.fill();

      // Render threat blips
      radarThreats.forEach((th) => {
        const bx = cx + Math.cos(th.angle) * (th.distance * radius);
        const by = cy + Math.sin(th.angle) * (th.distance * radius);

        ctx.fillStyle = th.level === "CRITICAL" ? "#ef4444" : "#f59e0b";
        ctx.beginPath();
        ctx.arc(bx, by, 6, 0, Math.PI * 2);
        ctx.fill();

        // Pulsing halo
        ctx.strokeStyle = th.level === "CRITICAL" ? "rgba(239, 68, 68, 0.5)" : "rgba(245, 158, 11, 0.5)";
        ctx.beginPath();
        ctx.arc(bx, by, 10, 0, Math.PI * 2);
        ctx.stroke();
      });

      animationFrameId = requestAnimationFrame(render);
    };

    render();

    return () => {
      cancelAnimationFrame(animationFrameId);
    };
  }, [radarThreats]);

  // Handle Instant Blast Radius Manual Quarantine
  const handleInstantQuarantine = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTargetInput.trim()) return;

    try {
      const res = await fetch("/api/blocklist/report", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          target: newTargetInput.trim(),
          target_type: "DOMAIN",
          threat_type: "MANUAL_SOC_CONTAINMENT",
          risk_level: "CRITICAL",
          source_platform: "SOC_CONSOLE",
          reason: "Manual Emergency Admin Quarantine"
        })
      });
      if (res.ok) {
        setNewTargetInput("");
      }
    } catch (err) {
      console.error("Failed to contain target", err);
    }
  };

  const isLight = theme === "light";
  const bgMain = isLight ? "#f1f5f9" : "#020617";
  const bgCard = isLight ? "#ffffff" : "#0b1329";
  const borderColor = isLight ? "#cbd5e1" : "#1e293b";
  const textColor = isLight ? "#0f172a" : "#f8fafc";
  const textMuted = isLight ? "#64748b" : "#94a3b8";

  return (
    <div style={{ backgroundColor: bgMain, color: textColor, minHeight: "100vh", fontFamily: "monospace", padding: "24px", transition: "background-color 0.2s, color 0.2s" }}>
      {/* Top Header Bar */}
      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", borderBottom: `1px solid ${borderColor}`, paddingBottom: "16px", marginBottom: "24px" }}>
        <div>
          <h1 style={{ margin: 0, fontSize: "24px", color: isLight ? "#0284c7" : "#38bdf8", letterSpacing: "2px" }}>
            🛡️ VEX CYBERGUARD :: NEXT.JS TACTICAL SOC
          </h1>
          <p style={{ margin: "4px 0 0 0", color: textMuted, fontSize: "13px" }}>
            Autonomous Multi-Tenant Deception, Mobile RASP & Blast Radius Containment
          </p>
        </div>

        {/* Real-Time WebSocket Status Indicator & Theme Switcher */}
        <div style={{ display: "flex", gap: "12px", alignItems: "center" }}>
          {/* Light / Dark Mode Switcher */}
          <button
            onClick={() => setTheme(isLight ? "dark" : "light")}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "6px",
              padding: "6px 14px",
              borderRadius: "6px",
              backgroundColor: isLight ? "#e2e8f0" : "#1e293b",
              color: isLight ? "#0f172a" : "#f8fafc",
              border: `1px solid ${isLight ? "#94a3b8" : "#334155"}`,
              cursor: "pointer",
              fontWeight: "bold",
              fontSize: "12px"
            }}
          >
            {isLight ? "🌙 Dark Mode" : "☀️ Light Mode"}
          </button>

          <div style={{ padding: "6px 12px", borderRadius: "4px", backgroundColor: wsConnected ? (isLight ? "#dcfce7" : "#064e3b") : (isLight ? "#fee2e2" : "#7f1d1d"), border: `1px solid ${wsConnected ? "#10b981" : "#ef4444"}` }}>
            <span style={{ color: wsConnected ? (isLight ? "#15803d" : "#34d399") : (isLight ? "#b91c1c" : "#fca5a5"), fontSize: "12px", fontWeight: "bold" }}>
              {wsConnected ? "● MESH SYNCHRONIZED (<1ms)" : "○ MESH OFFLINE"}
            </span>
          </div>
          <div style={{ padding: "6px 12px", borderRadius: "4px", backgroundColor: isLight ? "#e2e8f0" : "#1e293b", border: `1px solid ${borderColor}` }}>
            <span style={{ color: isLight ? "#0284c7" : "#38bdf8", fontSize: "12px" }}>
              SUBSCRIBERS: <strong>{activeSubscribers}</strong>
            </span>
          </div>
        </div>
      </header>

      {/* Main Grid */}
      <div style={{ display: "grid", gridTemplateColumns: "360px 1fr 420px", gap: "24px" }}>
        {/* Left Column: Canvas Radar & Telemetry */}
        <section style={{ backgroundColor: bgCard, border: `1px solid ${borderColor}`, borderRadius: "8px", padding: "16px" }}>
          <h2 style={{ fontSize: "15px", color: isLight ? "#059669" : "#00ffaa", margin: "0 0 12px 0", letterSpacing: "1px" }}>
            📡 ACTIVE RADAR ATTRIBUTION
          </h2>
          <div style={{ display: "flex", justifyContent: "center" }}>
            <canvas ref={canvasRef} width={320} height={320} style={{ borderRadius: "50%", border: `2px solid ${isLight ? "#05966933" : "#00ffaa33"}` }} />
          </div>
          <div style={{ marginTop: "16px", fontSize: "12px", color: textMuted, lineHeight: "1.6" }}>
            <div>● Detection Mode: <strong>Virtual HAL + Canary Bug</strong></div>
            <div>● Geo-Resolution: <strong>Real-time Intruder Attribution</strong></div>
            <div>● AI Bypass Latency: <strong>&lt; 0.42 ms</strong></div>
          </div>
        </section>

        {/* Center Column: Live Threat Feed & MITRE ATT&CK Mapping */}
        <section style={{ backgroundColor: bgCard, border: `1px solid ${borderColor}`, borderRadius: "8px", padding: "16px" }}>
          <h2 style={{ fontSize: "15px", color: "#38bdf8", margin: "0 0 12px 0" }}>
            ⚡ INCIDENT TELEMETRY & MITRE ATT&CK
          </h2>
          <div style={{ overflowY: "auto", maxHeight: "540px" }}>
            {events.map((evt) => (
              <div
                key={evt.event_id}
                onClick={() => setSelectedThreat(evt)}
                style={{
                  padding: "12px",
                  marginBottom: "8px",
                  borderRadius: "6px",
                  backgroundColor: selectedThreat?.event_id === evt.event_id ? "#1e293b" : "#0f172a",
                  borderLeft: `4px solid ${evt.risk_level === "CRITICAL" ? "#ef4444" : "#f59e0b"}`,
                  cursor: "pointer"
                }}
              >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <span style={{ fontWeight: "bold", color: "#f8fafc", fontSize: "13px" }}>{evt.threat_type}</span>
                  <span style={{ fontSize: "11px", padding: "2px 6px", borderRadius: "4px", backgroundColor: evt.risk_level === "CRITICAL" ? "#7f1d1d" : "#78350f", color: "#fff" }}>
                    {evt.risk_level} ({evt.risk_score})
                  </span>
                </div>
                <div style={{ fontSize: "12px", color: "#94a3b8", marginTop: "4px" }}>
                  Device: {evt.device_id} | Platform: {evt.platform || "ANDROID"}
                </div>
                {evt.mitre_attack && (
                  <div style={{ fontSize: "11px", color: "#38bdf8", marginTop: "4px" }}>
                    🎯 MITRE: [{evt.mitre_attack.technique_id}] {evt.mitre_attack.technique_name}
                  </div>
                )}
              </div>
            ))}
          </div>
        </section>

        {/* Right Column: Blast Radius Containment Mesh (Pillar 4) */}
        <section style={{ backgroundColor: bgCard, border: `1px solid ${borderColor}`, borderRadius: "8px", padding: "16px" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "12px" }}>
            <h2 style={{ fontSize: "15px", color: "#ef4444", margin: 0 }}>
              🛑 BLAST RADIUS CONTAINMENT
            </h2>
            <span style={{ fontSize: "11px", backgroundColor: "#7f1d1d", padding: "2px 8px", borderRadius: "4px", color: "#fca5a5" }}>
              GLOBAL REAL-TIME
            </span>
          </div>

          <p style={{ fontSize: "12px", color: "#94a3b8", margin: "0 0 16px 0" }}>
            The instant ANY endpoint flags a threat, it is contained globally across all devices within 1ms, bypassing AI for future hits.
          </p>

          {/* 1-Click Containment Trigger Form */}
          <form onSubmit={handleInstantQuarantine} style={{ display: "flex", gap: "8px", marginBottom: "16px" }}>
            <input
              type="text"
              placeholder="e.g. evil-phish.xyz or 192.168.1.1"
              value={newTargetInput}
              onChange={(e) => setNewTargetInput(e.target.value)}
              style={{
                flex: 1,
                padding: "8px 12px",
                borderRadius: "4px",
                backgroundColor: "#020617",
                border: "1px solid #334155",
                color: "#fff",
                fontSize: "12px"
              }}
            />
            <button
              type="submit"
              style={{
                padding: "8px 16px",
                backgroundColor: "#dc2626",
                color: "#fff",
                border: "none",
                borderRadius: "4px",
                fontWeight: "bold",
                fontSize: "12px",
                cursor: "pointer"
              }}
            >
              CONTAIN
            </button>
          </form>

          {/* Contained Targets Table */}
          <div style={{ overflowY: "auto", maxHeight: "420px" }}>
            {blocklist.map((b, i) => (
              <div key={i} style={{ padding: "8px 12px", backgroundColor: "#020617", borderRadius: "4px", marginBottom: "6px", border: "1px solid #1e293b" }}>
                <div style={{ display: "flex", justifyContent: "space-between", fontSize: "12px" }}>
                  <span style={{ color: "#fca5a5", fontWeight: "bold" }}>{b.target}</span>
                  <span style={{ color: "#34d399", fontSize: "10px" }}>BYPASS AI: TRUE</span>
                </div>
                <div style={{ fontSize: "11px", color: "#64748b", marginTop: "2px" }}>
                  Platform: {b.source_platform} | Reason: {b.reason}
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
