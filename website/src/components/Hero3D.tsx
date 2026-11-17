import React, { useEffect, useRef, useState, useCallback } from 'react';
import * as THREE from 'three';
import { sounds } from '../utils/audio';
import { 
  ShieldCheck, 
  RotateCcw, 
  Eye, 
  Layers, 
  Scan, 
  Radio, 
  Smartphone, 
  Zap, 
  Sparkles 
} from 'lucide-react';

interface Hero3DProps {
  onInteract?: () => void;
}

export const Hero3D: React.FC<Hero3DProps> = ({ onInteract }) => {
  const mountRef = useRef<HTMLDivElement>(null);
  const [activeAnglePreset, setActiveAnglePreset] = useState<'front' | 'isometric' | 'side' | 'back'>('isometric');
  const [isScanning, setIsScanning] = useState(false);
  const [isDragging, setIsDragging] = useState(false);
  const [scanCount, setScanCount] = useState(148);

  // Three.js and Canvas references
  const sceneRef = useRef<THREE.Scene | null>(null);
  const cameraRef = useRef<THREE.PerspectiveCamera | null>(null);
  const rendererRef = useRef<THREE.WebGLRenderer | null>(null);
  const phoneGroupRef = useRef<THREE.Group | null>(null);
  const screenTextureRef = useRef<THREE.CanvasTexture | null>(null);
  const offscreenCanvasRef = useRef<HTMLCanvasElement | null>(null);
  const offscreenCtxRef = useRef<CanvasRenderingContext2D | null>(null);
  
  // Animation and interaction state
  const targetRotation = useRef({ x: 0.25, y: -0.55 });
  const currentRotation = useRef({ x: 0.25, y: -0.55 });
  const mousePos = useRef({ x: 0, y: 0 });
  const isPointerDown = useRef(false);
  const lastPointerPos = useRef({ x: 0, y: 0 });
  const animFrameRef = useRef<number | null>(null);
  const scanTriggerTime = useRef<number>(0);
  const logEntriesRef = useRef<string[]>([
    'KERNEL: eBPF security filter initialized',
    'USB-C: No malicious HID inject detected',
    'SMS-BOMB: Heuristic drop rule armed',
    'CAMPHISH: Zero-trust lens guard active',
    'CACHE: Offline survival dataset loaded',
  ]);

  // Trigger simulated security scan on the 3D phone
  const triggerScan = useCallback(() => {
    sounds.playScan();
    setIsScanning(true);
    scanTriggerTime.current = performance.now();
    setScanCount((prev) => prev + 1);
    
    // Add dynamic log to screen terminal
    const events = [
      'SCAN: Deep audit completed on port 5173',
      'eBPF: Verified 0 BadUSB payload vulnerabilities',
      'SMS/CALL: 48 burst probes filtered',
      'SANDBOX: CamPhish trap verified safe',
      'CACHE ENGINE: All 12 Android modules nominal',
    ];
    const picked = events[Math.floor(Math.random() * events.length)];
    logEntriesRef.current = [picked, ...logEntriesRef.current.slice(0, 4)];

    if (onInteract) onInteract();

    setTimeout(() => {
      setIsScanning(false);
    }, 2400);
  }, [onInteract]);

  // Set camera/phone angle preset
  const setAngle = (preset: 'front' | 'isometric' | 'side' | 'back') => {
    setActiveAnglePreset(preset);
    sounds.playClick(650, 0.03);
    
    if (preset === 'front') {
      targetRotation.current = { x: 0, y: 0 };
    } else if (preset === 'isometric') {
      targetRotation.current = { x: 0.22, y: -0.52 };
    } else if (preset === 'side') {
      targetRotation.current = { x: 0.05, y: -1.35 };
    } else if (preset === 'back') {
      targetRotation.current = { x: 0.08, y: Math.PI };
    }
  };

  useEffect(() => {
    const container = mountRef.current;
    if (!container) return;

    const width = container.clientWidth;
    const height = container.clientHeight;

    // 1. Initialize Offscreen Canvas for VEX Android App Screen
    const canvas = document.createElement('canvas');
    canvas.width = 512;
    canvas.height = 1024;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    offscreenCanvasRef.current = canvas;
    offscreenCtxRef.current = ctx;

    // Create CanvasTexture for Three.js
    const screenTexture = new THREE.CanvasTexture(canvas);
    screenTexture.generateMipmaps = true;
    screenTexture.minFilter = THREE.LinearMipmapLinearFilter;
    screenTexture.magFilter = THREE.LinearFilter;
    screenTextureRef.current = screenTexture;

    // 2. Three.js Scene Setup
    const scene = new THREE.Scene();
    sceneRef.current = scene;

    const camera = new THREE.PerspectiveCamera(40, width / height, 0.1, 1000);
    camera.position.set(0, 0, 9.0);
    cameraRef.current = camera;

    const renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true, powerPreference: 'high-performance' });
    renderer.setSize(width, height);
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    renderer.shadowMap.enabled = true;
    container.innerHTML = '';
    container.appendChild(renderer.domElement);
    rendererRef.current = renderer;

    // 3. Lighting Setup: Vibrant Solid & Edge Accents
    const ambientLight = new THREE.AmbientLight(0xffffff, 1.2);
    scene.add(ambientLight);

    const dirBlue = new THREE.DirectionalLight(0x3B82F6, 4.0); // Solid Cyber Blue
    dirBlue.position.set(6, 6, 6);
    scene.add(dirBlue);

    const dirEmerald = new THREE.DirectionalLight(0x10B981, 3.2); // Solid Emerald Green
    dirEmerald.position.set(-6, -4, 5);
    scene.add(dirEmerald);

    const dirAmber = new THREE.DirectionalLight(0xF59E0B, 2.5); // Solid Amber / Gold
    dirAmber.position.set(0, 6, -4);
    scene.add(dirAmber);

    // 4. Main Phone Device Group
    const phoneGroup = new THREE.Group();
    scene.add(phoneGroup);
    phoneGroupRef.current = phoneGroup;

    // Dimensions for realistic smartphone chassis
    const phoneWidth = 2.45;
    const phoneHeight = 4.95;
    const phoneDepth = 0.22;

    // (A) Solid Phone Body / Outer Rim Chassis (Dark Slate with Metallic Edge)
    const bodyGeo = new THREE.BoxGeometry(phoneWidth, phoneHeight, phoneDepth);
    const bodyMat = new THREE.MeshStandardMaterial({
      color: 0x090D1A, // Midnight Obsidian
      metalness: 0.85,
      roughness: 0.2,
      flatShading: true,
    });
    const bodyMesh = new THREE.Mesh(bodyGeo, bodyMat);
    phoneGroup.add(bodyMesh);

    // (B) Chamfered Cyber Blue Border Rim
    const rimGeo = new THREE.BoxGeometry(phoneWidth + 0.04, phoneHeight + 0.04, phoneDepth * 0.7);
    const rimMat = new THREE.MeshStandardMaterial({
      color: 0x1E40AF, // Deep Cyber Blue
      metalness: 0.6,
      roughness: 0.3,
      flatShading: true,
    });
    const rimMesh = new THREE.Mesh(rimGeo, rimMat);
    phoneGroup.add(rimMesh);

    // (C) Front Screen Plane displaying the Dynamic VEX App Canvas
    const screenGeo = new THREE.PlaneGeometry(2.32, 4.78);
    const screenMat = new THREE.MeshBasicMaterial({
      map: screenTexture,
      toneMapped: false,
    });
    const screenMesh = new THREE.Mesh(screenGeo, screenMat);
    screenMesh.position.z = (phoneDepth / 2) + 0.005;
    phoneGroup.add(screenMesh);

    // (D) Front Camera Notch / Punch Hole
    const notchGeo = new THREE.CircleGeometry(0.06, 16);
    const notchMat = new THREE.MeshBasicMaterial({ color: 0x030712 });
    const notchMesh = new THREE.Mesh(notchGeo, notchMat);
    notchMesh.position.set(0, 2.22, (phoneDepth / 2) + 0.008);
    phoneGroup.add(notchMesh);

    // (E) Back Side Camera Module & Frosted Glass Back Plate
    const backCameraIslandGeo = new THREE.BoxGeometry(0.85, 1.25, 0.08);
    const backCameraMat = new THREE.MeshStandardMaterial({
      color: 0x111827,
      metalness: 0.7,
      roughness: 0.2,
      flatShading: true,
    });
    const backCameraIsland = new THREE.Mesh(backCameraIslandGeo, backCameraMat);
    backCameraIsland.position.set(0.6, 1.6, -(phoneDepth / 2) - 0.04);
    phoneGroup.add(backCameraIsland);

    // Camera Dual Lenses
    const lensGeo = new THREE.CylinderGeometry(0.2, 0.2, 0.04, 24);
    const lensMat = new THREE.MeshStandardMaterial({
      color: 0x1E3A8A,
      metalness: 0.9,
      roughness: 0.1,
    });
    const lens1 = new THREE.Mesh(lensGeo, lensMat);
    lens1.rotation.x = Math.PI / 2;
    lens1.position.set(0.6, 1.88, -(phoneDepth / 2) - 0.08);
    phoneGroup.add(lens1);

    const lens2 = new THREE.Mesh(lensGeo, lensMat);
    lens2.rotation.x = Math.PI / 2;
    lens2.position.set(0.6, 1.35, -(phoneDepth / 2) - 0.08);
    phoneGroup.add(lens2);

    // Back Plate Laser Engraving Plate: VEX LOG • BPUT HACKATHON 2026
    const backLogoGeo = new THREE.BoxGeometry(1.4, 0.7, 0.01);
    const backLogoMat = new THREE.MeshStandardMaterial({
      color: 0x1E293B,
      metalness: 0.5,
      roughness: 0.4,
      flatShading: true,
    });
    const backLogoMesh = new THREE.Mesh(backLogoGeo, backLogoMat);
    backLogoMesh.position.set(0, -0.6, -(phoneDepth / 2) - 0.005);
    phoneGroup.add(backLogoMesh);

    // (F) Side Buttons (Volume Rocker & Power Key)
    const buttonGeo = new THREE.BoxGeometry(0.04, 0.5, 0.08);
    const buttonMat = new THREE.MeshStandardMaterial({ color: 0x3B82F6, metalness: 0.8 });
    const volumeBtn = new THREE.Mesh(buttonGeo, buttonMat);
    volumeBtn.position.set(phoneWidth / 2 + 0.015, 0.8, 0);
    phoneGroup.add(volumeBtn);

    const powerBtnGeo = new THREE.BoxGeometry(0.04, 0.35, 0.08);
    const powerBtnMat = new THREE.MeshStandardMaterial({ color: 0x10B981, metalness: 0.8 });
    const powerBtn = new THREE.Mesh(powerBtnGeo, powerBtnMat);
    powerBtn.position.set(phoneWidth / 2 + 0.015, 0.0, 0);
    phoneGroup.add(powerBtn);

    // 5. Orbiting Solid Holographic Satellites (Representing VEX Pillars)
    const satellitesGroup = new THREE.Group();
    phoneGroup.add(satellitesGroup);

    // Satellite 1: eBPF Kernel Trap (Solid Emerald Octahedron)
    const sat1Geo = new THREE.OctahedronGeometry(0.38, 0);
    const sat1Mat = new THREE.MeshStandardMaterial({
      color: 0x10B981,
      roughness: 0.15,
      metalness: 0.6,
      flatShading: true,
    });
    const sat1 = new THREE.Mesh(sat1Geo, sat1Mat);
    sat1.position.set(2.4, 1.2, 0.4);
    satellitesGroup.add(sat1);

    // Satellite 2: Local Cache Engine (Solid Cyber Blue Cube)
    const sat2Geo = new THREE.BoxGeometry(0.5, 0.5, 0.5);
    const sat2Mat = new THREE.MeshStandardMaterial({
      color: 0x3B82F6,
      roughness: 0.2,
      metalness: 0.7,
      flatShading: true,
    });
    const sat2 = new THREE.Mesh(sat2Geo, sat2Mat);
    sat2.position.set(-2.3, -1.2, 0.5);
    satellitesGroup.add(sat2);

    // Satellite 3: Anti-Bombing SMS Guard (Solid Gold Tetrahedron)
    const sat3Geo = new THREE.TetrahedronGeometry(0.4, 0);
    const sat3Mat = new THREE.MeshStandardMaterial({
      color: 0xF59E0B,
      roughness: 0.2,
      metalness: 0.5,
      flatShading: true,
    });
    const sat3 = new THREE.Mesh(sat3Geo, sat3Mat);
    sat3.position.set(-1.9, 1.8, -0.6);
    satellitesGroup.add(sat3);

    // Satellite 4: Zero-Trust Orbit Ring
    const orbitRingGeo = new THREE.TorusGeometry(3.6, 0.025, 8, 48);
    const orbitRingMat = new THREE.MeshBasicMaterial({
      color: 0x3B82F6,
      wireframe: true,
      transparent: true,
      opacity: 0.45,
    });
    const orbitRing = new THREE.Mesh(orbitRingGeo, orbitRingMat);
    orbitRing.rotation.x = Math.PI / 2.5;
    satellitesGroup.add(orbitRing);

    // 6. Draw 2D VEX Android App Interface on Offscreen Canvas
    let radarSweepAngle = 0;
    let frameCounter = 0;

    const renderAppScreen = (time: number) => {
      if (!ctx) return;

      const now = performance.now();
      const isScanActive = (now - scanTriggerTime.current) < 2400;

      radarSweepAngle += isScanActive ? 0.08 : 0.028;

      // Clear Canvas Background (Dark Cyber Slate)
      ctx.fillStyle = '#070B14';
      ctx.fillRect(0, 0, 512, 1024);

      // Subtle Cyber Grid Pattern
      ctx.strokeStyle = '#111A2E';
      ctx.lineWidth = 1;
      for (let x = 0; x < 512; x += 32) {
        ctx.beginPath();
        ctx.moveTo(x, 0);
        ctx.lineTo(x, 1024);
        ctx.stroke();
      }
      for (let y = 0; y < 1024; y += 32) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(512, y);
        ctx.stroke();
      }

      // (A) Top Android Status Bar
      ctx.fillStyle = '#0F172A';
      ctx.fillRect(0, 0, 512, 54);
      ctx.strokeStyle = '#1E293B';
      ctx.lineWidth = 2;
      ctx.strokeRect(0, 0, 512, 54);

      ctx.fillStyle = '#94A3B8';
      ctx.font = 'bold 20px "Courier New", monospace';
      ctx.fillText('04:00 AM', 28, 36);

      // Status Bar Badges: 5G, OFFLINE CACHE, 100%
      ctx.fillStyle = '#10B981';
      ctx.font = 'bold 16px "Courier New", monospace';
      ctx.fillText('5G [STANDALONE]', 240, 36);

      ctx.fillStyle = '#F59E0B';
      ctx.fillText('⚡100%', 430, 36);

      // (B) App Header & Shield Branding
      ctx.fillStyle = '#0C1222';
      ctx.fillRect(20, 72, 472, 94);
      ctx.strokeStyle = isScanActive ? '#F59E0B' : '#3B82F6';
      ctx.lineWidth = 3;
      ctx.strokeRect(20, 72, 472, 94);

      // Pulsing Green/Amber Core Indicator Dot
      ctx.fillStyle = isScanActive ? '#F59E0B' : '#10B981';
      ctx.beginPath();
      ctx.arc(52, 119, 10, 0, Math.PI * 2);
      ctx.fill();

      ctx.fillStyle = '#FFFFFF';
      ctx.font = '900 24px "Segoe UI", sans-serif';
      ctx.fillText('VEX // CYBERGUARD', 76, 112);

      ctx.fillStyle = '#38BDF8';
      ctx.font = 'bold 14px "Courier New", monospace';
      ctx.fillText('ZERO-TRUST KERNEL SENTINEL v2.4', 76, 136);

      ctx.fillStyle = isScanActive ? '#FEF08A' : '#A7F3D0';
      ctx.font = 'bold 13px "Courier New", monospace';
      const statusBadge = isScanActive ? 'AUDIT IN PROGRESS' : 'ACTIVE ENFORCEMENT';
      ctx.fillText(`[ ${statusBadge} ]`, 280, 110);

      // (C) Interactive Radar Scanner Display
      const radarCenterX = 256;
      const radarCenterY = 270;
      const radarRadius = 90;

      // Radar Outer Ring
      ctx.strokeStyle = isScanActive ? '#F59E0B' : '#3B82F6';
      ctx.lineWidth = 3;
      ctx.beginPath();
      ctx.arc(radarCenterX, radarCenterY, radarRadius, 0, Math.PI * 2);
      ctx.stroke();

      // Radar Concentric Range Circles
      ctx.strokeStyle = '#1E293B';
      ctx.lineWidth = 1.5;
      [30, 60].forEach((r) => {
        ctx.beginPath();
        ctx.arc(radarCenterX, radarCenterY, r, 0, Math.PI * 2);
        ctx.stroke();
      });

      // Radar Crosshairs
      ctx.beginPath();
      ctx.moveTo(radarCenterX - radarRadius, radarCenterY);
      ctx.lineTo(radarCenterX + radarRadius, radarCenterY);
      ctx.moveTo(radarCenterX, radarCenterY - radarRadius);
      ctx.lineTo(radarCenterX, radarCenterY + radarRadius);
      ctx.stroke();

      // Radar Sweep Fan
      const grad = ctx.createRadialGradient(radarCenterX, radarCenterY, 5, radarCenterX, radarCenterY, radarRadius);
      grad.addColorStop(0, isScanActive ? 'rgba(245, 158, 11, 0.4)' : 'rgba(16, 185, 129, 0.45)');
      grad.addColorStop(1, 'rgba(16, 185, 129, 0.0)');

      ctx.fillStyle = grad;
      ctx.beginPath();
      ctx.moveTo(radarCenterX, radarCenterY);
      ctx.arc(radarCenterX, radarCenterY, radarRadius, radarSweepAngle, radarSweepAngle + 0.65);
      ctx.closePath();
      ctx.fill();

      // Radar Center Readout
      ctx.fillStyle = '#FFFFFF';
      ctx.font = '900 18px "Courier New", monospace';
      ctx.textAlign = 'center';
      ctx.fillText(isScanActive ? 'SCANNING' : 'PROTECTED', radarCenterX, radarCenterY + 5);

      ctx.fillStyle = '#10B981';
      ctx.font = 'bold 12px "Courier New", monospace';
      ctx.fillText('0 ANOMALIES DETECTED', radarCenterX, radarCenterY + 24);
      ctx.textAlign = 'left';

      // (D) Active Security Modules List (Real features built at BPUT)
      const modules = [
        { name: 'BAD USB SENTINEL', sub: 'eBPF Kernel I/O Trap', status: 'ACTIVE', color: '#10B981' },
        { name: 'CALL / SMS BOMBER', sub: 'Heuristic Token Drop', status: 'FILTERED', color: '#10B981' },
        { name: 'CAMPHISH SHIELD', sub: 'Anti-Impersonation Hook', status: 'ARMED', color: '#10B981' },
        { name: 'VEX ZERO-TRUST BROWSER', sub: 'Isolated Sandbox DOM', status: 'SECURE', color: '#3B82F6' },
        { name: 'OFFLINE CACHE ENGINE', sub: 'Android Native Standalone', status: 'READY', color: '#06B6D4' },
      ];

      let cardY = 380;
      modules.forEach((mod, idx) => {
        ctx.fillStyle = '#0A0F1D';
        ctx.fillRect(20, cardY, 472, 54);
        ctx.strokeStyle = '#1E293B';
        ctx.lineWidth = 1.5;
        ctx.strokeRect(20, cardY, 472, 54);

        // Status bar on left
        ctx.fillStyle = mod.color;
        ctx.fillRect(20, cardY, 6, 54);

        // Module Name
        ctx.fillStyle = '#FFFFFF';
        ctx.font = '900 15px "Courier New", monospace';
        ctx.fillText(mod.name, 38, cardY + 24);

        // Subtitle
        ctx.fillStyle = '#64748B';
        ctx.font = '12px "Courier New", monospace';
        ctx.fillText(mod.sub, 38, cardY + 44);

        // Status Badge Pill
        ctx.fillStyle = mod.color;
        ctx.font = 'bold 12px "Courier New", monospace';
        ctx.fillText(`● ${mod.status}`, 370, cardY + 34);

        cardY += 64;
      });

      // (E) Live Forensic Terminal Stream on Screen
      ctx.fillStyle = '#050811';
      ctx.fillRect(20, 715, 472, 180);
      ctx.strokeStyle = '#1E293B';
      ctx.lineWidth = 2;
      ctx.strokeRect(20, 715, 472, 180);

      // Terminal Header
      ctx.fillStyle = '#0F172A';
      ctx.fillRect(20, 715, 472, 28);
      ctx.fillStyle = '#38BDF8';
      ctx.font = 'bold 12px "Courier New", monospace';
      ctx.fillText('>_ LIVE FORENSIC TELEMETRY STREAM', 32, 734);

      // Terminal Log Lines
      ctx.font = '12px "Courier New", monospace';
      logEntriesRef.current.forEach((log, lIdx) => {
        ctx.fillStyle = lIdx === 0 ? (isScanActive ? '#FBBF24' : '#34D399') : '#94A3B8';
        ctx.fillText(`$ ${log}`, 32, 765 + (lIdx * 24));
      });

      // (F) Bottom Android App Dock
      ctx.fillStyle = '#0F172A';
      ctx.fillRect(20, 915, 472, 60);
      ctx.strokeStyle = '#3B82F6';
      ctx.lineWidth = 2;
      ctx.strokeRect(20, 915, 472, 60);

      ctx.fillStyle = '#FFFFFF';
      ctx.font = '900 14px "Courier New", monospace';
      ctx.textAlign = 'center';
      ctx.fillText('[ INITIATE DEEP AUDIT ]', 256, 950);
      ctx.textAlign = 'left';

      // (G) Scanning Laser Line Effect across the screen
      if (isScanActive) {
        const scanProgress = ((now - scanTriggerTime.current) % 1200) / 1200;
        const lineY = scanProgress * 1024;
        ctx.strokeStyle = '#F59E0B';
        ctx.lineWidth = 6;
        ctx.beginPath();
        ctx.moveTo(0, lineY);
        ctx.lineTo(512, lineY);
        ctx.stroke();

        ctx.fillStyle = 'rgba(245, 158, 11, 0.15)';
        ctx.fillRect(0, Math.max(0, lineY - 40), 512, 40);
      }

      // Notify Three.js that texture needs update
      screenTexture.needsUpdate = true;
    };

    // 7. Mouse and Pointer Drag Handlers for 3D Orbiting
    const handlePointerDown = (e: PointerEvent) => {
      isPointerDown.current = true;
      setIsDragging(true);
      lastPointerPos.current = { x: e.clientX, y: e.clientY };
    };

    const handlePointerMove = (e: PointerEvent) => {
      const rect = container.getBoundingClientRect();
      // Normalized coordinates for subtle parallax
      mousePos.current = {
        x: ((e.clientX - rect.left) / width - 0.5) * 2,
        y: ((e.clientY - rect.top) / height - 0.5) * 2,
      };

      if (isPointerDown.current) {
        const deltaX = e.clientX - lastPointerPos.current.x;
        const deltaY = e.clientY - lastPointerPos.current.y;
        targetRotation.current.y += deltaX * 0.01;
        targetRotation.current.x += deltaY * 0.01;
        // Clamp vertical tilt
        targetRotation.current.x = Math.max(-Math.PI / 2.5, Math.min(Math.PI / 2.5, targetRotation.current.x));
        lastPointerPos.current = { x: e.clientX, y: e.clientY };
      }
    };

    const handlePointerUp = () => {
      isPointerDown.current = false;
      setIsDragging(false);
    };

    container.addEventListener('pointerdown', handlePointerDown);
    window.addEventListener('pointermove', handlePointerMove);
    window.addEventListener('pointerup', handlePointerUp);

    // 8. Main 60fps Animation Loop
    let time = 0;
    const animate = () => {
      time += 0.015;
      frameCounter++;

      // Smooth damping interpolation (lerp)
      currentRotation.current.x += (targetRotation.current.x - currentRotation.current.x) * 0.08;
      currentRotation.current.y += (targetRotation.current.y - currentRotation.current.y) * 0.08;

      if (phoneGroupRef.current) {
        // Apply rotation + subtle mouse parallax
        const parallaxX = mousePos.current.y * 0.15;
        const parallaxY = mousePos.current.x * 0.2;

        phoneGroupRef.current.rotation.x = currentRotation.current.x + parallaxX;
        phoneGroupRef.current.rotation.y = currentRotation.current.y + parallaxY;

        // Subtle floating / breathing animation perfectly centered
        phoneGroupRef.current.position.y = -0.12 + Math.sin(time * 1.5) * 0.05;

        // Orbit satellites
        sat1.rotation.y += 0.03;
        sat1.rotation.x += 0.02;
        sat2.rotation.y += 0.025;
        sat2.rotation.z += 0.02;
        sat3.rotation.x += 0.03;
        orbitRing.rotation.z += 0.005;
      }

      // Update offscreen 2D canvas texture
      renderAppScreen(time);

      renderer.render(scene, camera);
      animFrameRef.current = requestAnimationFrame(animate);
    };

    animFrameRef.current = requestAnimationFrame(animate);

    // 9. Resize Handler
    const handleResize = () => {
      if (!container || !camera || !renderer) return;
      const newW = container.clientWidth;
      const newH = container.clientHeight;
      camera.aspect = newW / newH;
      camera.updateProjectionMatrix();
      renderer.setSize(newW, newH);
    };

    window.addEventListener('resize', handleResize);

    return () => {
      if (animFrameRef.current) cancelAnimationFrame(animFrameRef.current);
      container.removeEventListener('pointerdown', handlePointerDown);
      window.removeEventListener('pointermove', handlePointerMove);
      window.removeEventListener('pointerup', handlePointerUp);
      window.removeEventListener('resize', handleResize);
      renderer.dispose();
      screenTexture.dispose();
      bodyGeo.dispose();
      screenGeo.dispose();
    };
  }, []);

  return (
    <div className="relative w-full h-[520px] sm:h-[620px] lg:h-[660px] bg-[#070B14] rounded-2xl border-4 border-[#1E293B] shadow-2xl overflow-hidden select-none flex flex-col justify-between">
      
      {/* Top Cyber HUD Bar */}
      <div className="z-20 px-4 py-3 bg-[#0B0F1D]/90 backdrop-blur-md border-b-2 border-[#1E293B] flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <div className="w-3 h-3 rounded-full bg-emerald-500 animate-pulse" />
          <div>
            <div className="flex items-center gap-2">
              <span className="font-mono text-xs font-black text-white uppercase tracking-wider">
                VEX KERNEL 3D REACTIVE RUNTIME
              </span>
              <span className="px-2 py-0.5 rounded text-[10px] font-mono font-bold bg-blue-500/20 text-blue-400 border border-blue-500/30">
                ANDROID SUITE
              </span>
            </div>
            <div className="text-[11px] font-mono text-slate-400">
              Interactive 360° Device Canvas • Drag or Tilt to Inspect
            </div>
          </div>
        </div>

        {/* Live Threat Counter Badge */}
        <div className="flex items-center gap-2">
          <div className="px-3 py-1 bg-[#101726] border border-[#1E293B] rounded-lg flex items-center gap-2">
            <Radio className="w-3.5 h-3.5 text-emerald-400 animate-spin" />
            <span className="text-xs font-mono font-bold text-slate-300">Audits:</span>
            <span className="text-xs font-mono font-black text-amber-400">{scanCount}</span>
          </div>
        </div>
      </div>

      {/* 3D WebGL Canvas Mount */}
      <div 
        ref={mountRef} 
        className="relative w-full flex-1 cursor-grab active:cursor-grabbing touch-none"
        title="Click and drag to rotate device 360 degrees"
      />

      {/* Dragging Feedback Watermark */}
      {isDragging && (
        <div className="absolute top-20 left-1/2 -translate-x-1/2 z-20 pointer-events-none px-3 py-1 bg-blue-600 text-white font-mono text-xs font-bold rounded-full shadow-lg">
          360° ORBIT ACTIVE
        </div>
      )}

      {/* Bottom Floating Control Bar */}
      <div className="z-20 px-4 py-3 bg-[#0B0F1D]/95 backdrop-blur-md border-t-2 border-[#1E293B] flex flex-wrap items-center justify-between gap-3">
        
        {/* Preset Angle Switchers */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
          <span className="text-[11px] font-mono font-bold text-slate-400 mr-1 hidden sm:inline">
            CAMERA:
          </span>

          <button
            onClick={() => setAngle('front')}
            className={`px-2.5 py-1.5 rounded-lg text-xs font-mono font-bold transition-all flex items-center gap-1.5 border-2 ${
              activeAnglePreset === 'front'
                ? 'bg-blue-600 text-white border-blue-400 shadow-sm'
                : 'bg-[#101726] text-slate-300 border-[#1E293B] hover:border-slate-500'
            }`}
          >
            <Smartphone className="w-3.5 h-3.5" />
            <span>Front UI</span>
          </button>

          <button
            onClick={() => setAngle('isometric')}
            className={`px-2.5 py-1.5 rounded-lg text-xs font-mono font-bold transition-all flex items-center gap-1.5 border-2 ${
              activeAnglePreset === 'isometric'
                ? 'bg-blue-600 text-white border-blue-400 shadow-sm'
                : 'bg-[#101726] text-slate-300 border-[#1E293B] hover:border-slate-500'
            }`}
          >
            <Layers className="w-3.5 h-3.5" />
            <span>3D Iso</span>
          </button>

          <button
            onClick={() => setAngle('side')}
            className={`px-2.5 py-1.5 rounded-lg text-xs font-mono font-bold transition-all flex items-center gap-1.5 border-2 ${
              activeAnglePreset === 'side'
                ? 'bg-blue-600 text-white border-blue-400 shadow-sm'
                : 'bg-[#101726] text-slate-300 border-[#1E293B] hover:border-slate-500'
            }`}
          >
            <Eye className="w-3.5 h-3.5" />
            <span>Side Rails</span>
          </button>

          <button
            onClick={() => setAngle('back')}
            className={`px-2.5 py-1.5 rounded-lg text-xs font-mono font-bold transition-all flex items-center gap-1.5 border-2 ${
              activeAnglePreset === 'back'
                ? 'bg-blue-600 text-white border-blue-400 shadow-sm'
                : 'bg-[#101726] text-slate-300 border-[#1E293B] hover:border-slate-500'
            }`}
          >
            <ShieldCheck className="w-3.5 h-3.5" />
            <span>Back Plate</span>
          </button>
        </div>

        {/* Action Button: Trigger Scan */}
        <div className="flex items-center gap-2">
          <button
            onClick={triggerScan}
            disabled={isScanning}
            className={`px-4 py-2 rounded-xl font-mono text-xs font-black uppercase tracking-wider flex items-center gap-2 transition-all shadow-md ${
              isScanning
                ? 'bg-amber-500 text-slate-950 animate-pulse border-2 border-amber-300 cursor-wait'
                : 'bg-emerald-600 hover:bg-emerald-500 text-white border-2 border-emerald-400 hover:scale-[1.02] active:scale-95'
            }`}
          >
            {isScanning ? (
              <>
                <Zap className="w-4 h-4 animate-spin" />
                <span>AUDITING KERNEL...</span>
              </>
            ) : (
              <>
                <Scan className="w-4 h-4" />
                <span>SIMULATE LIVE SCAN</span>
              </>
            )}
          </button>

          {/* Reset Angle Button */}
          <button
            onClick={() => setAngle('isometric')}
            title="Reset to default angle"
            className="p-2 rounded-xl bg-[#101726] border-2 border-[#1E293B] text-slate-400 hover:text-white hover:border-slate-500 transition-all"
          >
            <RotateCcw className="w-4 h-4" />
          </button>
        </div>

      </div>

    </div>
  );
};
