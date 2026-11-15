import os
import subprocess

html_path = r'C:\vex\test.html'
shot_path = r'C:\vex\test_shot.png'

with open(html_path, 'w', encoding='utf-8') as f:
    f.write('<html><body style="background:#070a10; color:#00ff9d;"><h1>TEST SHOT</h1></body></html>')

edge = r'C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe'
cmd = [edge, '--headless', '--disable-gpu', f'--screenshot={shot_path}', '--window-size=1200,800', html_path]
subprocess.run(cmd)

print('Shot exists:', os.path.exists(shot_path))
if os.path.exists(shot_path):
    print('Size:', os.path.getsize(shot_path))
