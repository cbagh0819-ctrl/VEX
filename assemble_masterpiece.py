# -*- coding: utf-8 -*-
"""
VEX Masterpiece Assembler
Assembles all sections into C:\vex\VEX_Master_Hackathon_Blueprint.html
and compiles into high-resolution Master PDF via Headless Edge.
"""
import os
import subprocess
import time

HTML_FILE = r"C:\vex\VEX_Master_Hackathon_Blueprint.html"
PDF_FILE_VEX = r"C:\vex\VEX_Master_Hackathon_Blueprint.pdf"
PDF_FILE_DOWNLOADS = r"C:\Users\HP\Downloads\VEX_Master_Hackathon_Blueprint.pdf"

from build_ultimate_html import get_head
from build_ultimate_section1 import get_cover_and_stack

# Read the remaining sections from our modular files
# We will read part1, part2, part3, part4, part5
import generate_blueprint_part1
import generate_blueprint_part2
import generate_blueprint_part3
import generate_blueprint_part4
import generate_blueprint_part5

# Load section text
with open(r"C:\vex\generate_blueprint_part1.py", "r", encoding="utf-8") as f:
    text_p1 = f.read()
# Extract part1_content
start_p1 = text_p1.find('<!-- ==================== SECTION 1: PLAN MAP ANALYSIS ==================== -->')
end_p1 = text_p1.find('<div class="page-break"></div>', start_p1) + len('<div class="page-break"></div>')
section_plan_map = text_p1[start_p1:end_p1].replace('1. Architectural Plan Map', '3. Architectural Plan Map')

with open(r"C:\vex\generate_blueprint_part2.py", "r", encoding="utf-8") as f:
    text_p2 = f.read()
start_p2 = text_p2.find('<!-- ==================== SECTION 2: THE 7 CORE SCREENS ==================== -->')
end_p2 = text_p2.find('<div class="page-break"></div>', start_p2) + len('<div class="page-break"></div>')
section_7_screens = text_p2[start_p2:end_p2].replace('2. The 7 Core Screens', '4. The 7 Core Screens')

with open(r"C:\vex\generate_blueprint_part3.py", "r", encoding="utf-8") as f:
    text_p3 = f.read()
start_p3 = text_p3.find('<!-- ==================== SECTION 3: FEATURES 1 TO 9 (CURRENT FEATURES) ==================== -->')
end_p3 = text_p3.find('<div class="page-break"></div>', start_p3) + len('<div class="page-break"></div>')
section_features_1_9 = text_p3[start_p3:end_p3].replace('3. Technical Specifications: Features 1 to 9', '5. Technical Specifications: Features 1 to 9')

with open(r"C:\vex\generate_blueprint_part4.py", "r", encoding="utf-8") as f:
    text_p4 = f.read()
start_p4 = text_p4.find('<!-- ==================== SECTION 4: FEATURES 10 TO 17 (FUTURE / ADVANCED MODULES) ==================== -->')
end_p4 = text_p4.find('<div class="page-break"></div>', start_p4) + len('<div class="page-break"></div>')
section_features_10_17 = text_p4[start_p4:end_p4].replace('4. Technical Specifications: Features 10 to 17', '6. Technical Specifications: Features 10 to 17')

with open(r"C:\vex\generate_blueprint_part5.py", "r", encoding="utf-8") as f:
    text_p5 = f.read()
start_p5 = text_p5.find('<!-- ==================== SECTION 5: THE 3 LIVE STAGE SHOWSTOPPERS ==================== -->')
end_p5 = text_p5.find('</html>', start_p5) + len('</html>')
section_showstoppers_and_roadmap = text_p5[start_p5:end_p5]
section_showstoppers_and_roadmap = section_showstoppers_and_roadmap.replace('5. The 3 Live Stage Demonstration Protocols', '7. The 3 Live Stage Demonstration Protocols')
section_showstoppers_and_roadmap = section_showstoppers_and_roadmap.replace('6. 36-Hour Hackathon Execution Roadmap', '8. 36-Hour Hackathon Execution Roadmap')

# Assemble full masterpiece HTML
full_html = (
    get_head() +
    get_cover_and_stack() +
    section_plan_map + "\n\n" +
    section_7_screens + "\n\n" +
    section_features_1_9 + "\n\n" +
    section_features_10_17 + "\n\n" +
    section_showstoppers_and_roadmap
)

with open(HTML_FILE, "w", encoding="utf-8") as f:
    f.write(full_html)

print(f"Masterpiece HTML generated successfully: {len(full_html)} bytes.")

# Now compile to PDF using Edge Headless
edge_path = r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
if os.path.exists(edge_path):
    print("Compiling Master PDF via Edge Headless...")
    cmd = [
        edge_path,
        "--headless=new",
        "--disable-gpu",
        "--run-all-compositor-stages-before-draw",
        f"--print-to-pdf={PDF_FILE_VEX}",
        "--no-pdf-header-footer",
        f"file:///{HTML_FILE.replace(chr(92), '/')}"
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    time.sleep(2)
    if os.path.exists(PDF_FILE_VEX):
        pdf_size = os.path.getsize(PDF_FILE_VEX)
        print(f"PDF generated successfully: {pdf_size} bytes.")
        # Copy to Downloads
        import shutil
        shutil.copy2(PDF_FILE_VEX, PDF_FILE_DOWNLOADS)
        print(f"PDF copied to {PDF_FILE_DOWNLOADS}")
    else:
        print("PDF was not found after execution.")
else:
    print("Edge path not found.")
