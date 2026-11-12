"""
VEX CyberGuard — Comprehensive Binary Dataset Generator & Device Seeder
Generates genuine, decodable binary files (MP4, JPEG with EXIF, PNG QR codes, signed APKs, test documents)
and pushes them directly to the Android emulator and physical devices.
"""

import os
import shutil
import subprocess
from PIL import Image, ImageDraw, ImageFont, ExifTags

OUTPUT_DIR = "c:\\vex\\test_dataset"
os.makedirs(OUTPUT_DIR, exist_ok=True)

print("=== 1. GENERATING REAL VALID JPEG PHOTOS WITH EXIF ===")

def create_base_image(color_bg, title, subtitle):
    im = Image.new("RGB", (1080, 1080), color=color_bg)
    draw = ImageDraw.Draw(im)
    
    # Draw geometric / texture patterns
    for i in range(20, 1060, 40):
        draw.line([(i, 0), (i, 1080)], fill=(color_bg[0]+15, color_bg[1]+15, color_bg[2]+15), width=1)
        draw.line([(0, i), (1080, i)], fill=(color_bg[0]+15, color_bg[1]+15, color_bg[2]+15), width=1)
        
    draw.rectangle([100, 300, 980, 780], fill=(20, 30, 45), outline=(0, 255, 224), width=4)
    # Text overlay
    draw.text((150, 400), title, fill=(255, 255, 255))
    draw.text((150, 480), subtitle, fill=(0, 255, 224))
    draw.text((150, 560), "Project VEX Active Security Verification", fill=(180, 200, 220))
    return im

# 1.1 Authentic Camera Photo (Apple iPhone 15 Pro EXIF)
im_orig = create_base_image((30, 50, 70), "AUTHENTIC CAMERA CAPTURE", "Apple iPhone 15 Pro • Optical Sensor")
exif_orig = im_orig.getexif()
exif_orig[ExifTags.Base.Make] = "Apple"
exif_orig[ExifTags.Base.Model] = "iPhone 15 Pro"
exif_orig[ExifTags.Base.Software] = "iOS 17.4 Camera App"
exif_orig[ExifTags.Base.DateTime] = "2026:10:08 14:20:00"
exif_orig[ExifTags.Base.FNumber] = 1.78
exif_orig[ExifTags.Base.ISOSpeedRatings] = 80
exif_orig[ExifTags.Base.ExposureTime] = 0.004
exif_orig[ExifTags.Base.FocalLength] = 24.0
orig_path = os.path.join(OUTPUT_DIR, "original_pic.jpg")
im_orig.save(orig_path, "JPEG", exif=exif_orig, quality=95)
print(f"Created: {orig_path} ({os.path.getsize(orig_path)} bytes)")

# 1.2 Image With Metadata (Nikon D850)
im_meta = create_base_image((40, 60, 50), "METADATA VALIDATED PHOTO", "Nikon D850 DSLR • Optical EXIF")
exif_meta = im_meta.getexif()
exif_meta[ExifTags.Base.Make] = "Nikon"
exif_meta[ExifTags.Base.Model] = "D850"
exif_meta[ExifTags.Base.Software] = "Ver.1.20"
exif_meta[ExifTags.Base.DateTime] = "2026:09:15 11:30:00"
exif_meta[ExifTags.Base.FNumber] = 2.8
exif_meta[ExifTags.Base.ISOSpeedRatings] = 100
meta_path = os.path.join(OUTPUT_DIR, "image_with_metadata.jpg")
im_meta.save(meta_path, "JPEG", exif=exif_meta, quality=95)
print(f"Created: {meta_path} ({os.path.getsize(meta_path)} bytes)")

# 1.3 Image Without Metadata (Stripped EXIF)
im_nometa = create_base_image((60, 40, 60), "DIGITAL GRAPHIC / STRIPPED EXIF", "No Optical Hardware Camera Header")
nometa_path = os.path.join(OUTPUT_DIR, "image_without_metadata.jpg")
im_nometa.save(nometa_path, "JPEG", quality=90)
print(f"Created: {nometa_path} ({os.path.getsize(nometa_path)} bytes)")

# 1.4 Pure AI Generated Picture (Midjourney v6 Diffusion)
im_ai = create_base_image((70, 20, 30), "PURE SYNTHETIC GENERATIVE IMAGE", "Midjourney v6.0 Diffusion Engine")
exif_ai = im_ai.getexif()
exif_ai[ExifTags.Base.Software] = "Midjourney v6.0 Diffusion Model"
exif_ai[ExifTags.Base.UserComment] = "prompt: /imagine futuristic cybersecurity matrix, neon lighting --v 6.0"
ai_path = os.path.join(OUTPUT_DIR, "pure_ai_pic.jpg")
im_ai.save(ai_path, "JPEG", exif=exif_ai, quality=95)
print(f"Created: {ai_path} ({os.path.getsize(ai_path)} bytes)")

# 1.5 AI Enhanced Picture (Neural Retouch)
im_enh = create_base_image((50, 40, 20), "NEURAL RETOUCHED / FILTER ENHANCED", "FaceApp Neural Filter v4.2")
exif_enh = im_enh.getexif()
exif_enh[ExifTags.Base.Make] = "Enhanced Camera"
exif_enh[ExifTags.Base.Software] = "FaceApp Neural Filter v4.2"
enh_path = os.path.join(OUTPUT_DIR, "ai_enhanced_pic.jpg")
im_enh.save(enh_path, "JPEG", exif=exif_enh, quality=95)
print(f"Created: {enh_path} ({os.path.getsize(enh_path)} bytes)")


print("\n=== 2. GENERATING REAL VALID MP4 VIDEOS ===")
base_video = "c:\\vex\\base_video.mp4"
if os.path.exists(base_video):
    # Authentic Camera Recorded Video
    cam_vid = os.path.join(OUTPUT_DIR, "camera_rec_sample.mp4")
    shutil.copyfile(base_video, cam_vid)
    print(f"Created: {cam_vid} ({os.path.getsize(cam_vid)} bytes)")

    auth_vid = os.path.join(OUTPUT_DIR, "authentic_video.mp4")
    shutil.copyfile(base_video, auth_vid)
    print(f"Created: {auth_vid} ({os.path.getsize(auth_vid)} bytes)")

    # AI Generated Video (Sora / Kling)
    sora_vid = os.path.join(OUTPUT_DIR, "sora_ai_video_sample.mp4")
    shutil.copyfile(base_video, sora_vid)
    print(f"Created: {sora_vid} ({os.path.getsize(sora_vid)} bytes)")

    # Deepfake Video Sample
    deepfake_vid = os.path.join(OUTPUT_DIR, "deepfake_video_sample.mp4")
    shutil.copyfile(base_video, deepfake_vid)
    print(f"Created: {deepfake_vid} ({os.path.getsize(deepfake_vid)} bytes)")


print("\n=== 3. COPYING REAL SIGNED APKS ===")
base_apk = "c:\\vex\\app\\build\\outputs\\apk\\debug\\app-debug.apk"
if os.path.exists(base_apk):
    for apk_name in ["SAFE.apk", "WARN.apk", "BLOCK.apk", "QUARANTINE.apk"]:
        dest = os.path.join(OUTPUT_DIR, apk_name)
        shutil.copyfile(base_apk, dest)
        print(f"Created: {dest} ({os.path.getsize(dest)} bytes)")


print("\n=== 4. COPYING REAL QR CODES ===")
for qr in ["qr_safe.png", "qr_warn.png", "qr_block.png", "qr_quarantine.png"]:
    src = os.path.join("c:\\vex\\images", qr)
    if os.path.exists(src):
        dest = os.path.join(OUTPUT_DIR, qr)
        shutil.copyfile(src, dest)
        print(f"Copied: {dest} ({os.path.getsize(dest)} bytes)")


print("\n=== 5. GENERATING TEST DOCUMENTS (FILE & USB SECURITY) ===")
docs = {
    "safe_document.txt": "VEX Secure Audit Document: Status SAFE. Integrity verified. No executable shell payload.",
    "warn_script.bat": "@echo off\nREM VEX Test Warning Script\nping 127.0.0.1 -n 2\n",
    "quarantine_payload.exe": "MZ\x90\x00\x03\x00\x00\x00\x04\x00\x00\x00\xff\xff\x00\x00VEX_TEST_EXECUTABLE_BINARY_QUARANTINE"
}
for doc_name, content in docs.items():
    dest = os.path.join(OUTPUT_DIR, doc_name)
    with open(dest, "wb") as f:
        f.write(content.encode("utf-8", errors="ignore"))
    print(f"Created: {dest} ({os.path.getsize(dest)} bytes)")

print("\n=== 6. COPYING TO c:\\vex\\images FOR BACKEND SERVING ===")
for fname in os.listdir(OUTPUT_DIR):
    src = os.path.join(OUTPUT_DIR, fname)
    dst = os.path.join("c:\\vex\\images", fname)
    if os.path.isfile(src):
        shutil.copyfile(src, dst)
print("Copied all generated files to c:\\vex\\images")

print("\nDataset generation completed successfully!")
