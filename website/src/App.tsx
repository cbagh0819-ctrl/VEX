import React, { useState } from 'react';
import { Navbar } from './components/Navbar';
import { Hero3D } from './components/Hero3D';
import { HackathonGlory } from './components/HackathonGlory';
import { HackathonJourney } from './components/HackathonJourney';
import { ProblemStatement09 } from './components/ProblemStatement09';
import { InteractiveThreatLab } from './components/InteractiveThreatLab';
import { ProductFeatures } from './components/ProductFeatures';
import { TeamSection } from './components/TeamSection';
import { DownloadModal } from './components/DownloadModal';
import { Footer } from './components/Footer';
import { GithubIcon } from './components/Icons';
import { triggerVictoryConfetti } from './utils/confetti';
import { sounds } from './utils/audio';
import { 
  Award, 
  Terminal, 
  Clock
} from 'lucide-react';

export function App() {
  const [downloadModalOpen, setDownloadModalOpen] = useState(false);

  const handleCelebrate = () => {
    sounds.playFanfare();
    triggerVictoryConfetti();
  };

  return (
    <div className="min-h-screen bg-[#06080D] text-slate-100 flex flex-col font-sans selection:bg-blue-600 selection:text-white">
      
      {/* Navigation Bar */}
      <Navbar
        onOpenDownload={() => setDownloadModalOpen(true)}
        onOpenConfetti={handleCelebrate}
      />

      {/* Main Content Area */}
      <main className="flex-1">
        
        {/* HERO SECTION */}
        <section className="relative pt-12 pb-20 border-b-2 border-[#1E293B] overflow-hidden">
          {/* Subtle Cyber Grids */}
          <div className="absolute inset-0 bg-cyber-grid opacity-25 pointer-events-none" />
          <div className="absolute top-1/4 left-1/2 -translate-x-1/2 w-[700px] h-[500px] bg-blue-600/10 rounded-full blur-[140px] pointer-events-none" />

          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
            
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
              
              {/* Left Column: Hero Content */}
              <div className="lg:col-span-6 space-y-6">
                
                {/* 3rd Prize & BPUT Hackathon Tag */}
                <div className="flex flex-wrap items-center gap-2">
                  <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-xl bg-amber-500/15 border-2 border-amber-500 text-amber-400 font-mono text-xs font-bold tracking-wide shadow-solid-gold">
                    <Award className="w-4 h-4 fill-amber-400" />
                    <span>3RD PRIZE WINNER</span>
                  </div>

                  <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-[#0F172A] border-2 border-[#1E293B] text-slate-300 font-mono text-xs font-semibold">
                    <span className="w-2 h-2 rounded-full bg-blue-500" />
                    <span>BPUT HACKATHON 2026</span>
                  </div>
                </div>

                {/* Primary Headline */}
                <div className="space-y-2">
                  <div className="text-xs font-mono uppercase tracking-widest text-blue-400 font-bold flex items-center gap-2">
                    <span>Problem Statement 09: CYBERGUARD</span>
                    <span className="text-slate-600">/</span>
                    <span className="text-emerald-400">OKCL Partnered</span>
                  </div>

                  <h1 className="text-4xl sm:text-6xl font-heading font-black text-white tracking-tight leading-[1.08]">
                    VEX <span className="text-blue-500">//</span> <br />
                    <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-indigo-200 to-blue-500">
                      Trust Nothing.
                    </span> <br />
                    <span>Explain Everything.</span>
                  </h1>
                </div>

                {/* Lead Subtext */}
                <p className="text-base sm:text-lg text-slate-300 leading-relaxed max-w-xl font-normal">
                  Next-generation <strong className="text-white font-semibold">Explainable Zero-Trust AI (XAI)</strong> cyber threat, 
                  phishing impersonation, and forensic log intelligence platform. Engineered by 
                  <strong className="text-amber-400 font-semibold"> Team VEX LOG</strong> from <a href="https://pmec.ac.in/" target="_blank" rel="noopener noreferrer" className="text-blue-400 hover:underline font-semibold">Parala Maharaja Engineering College (PMEC)</a>, Berhampur, 
                  mentored by <strong className="text-white font-semibold">Mr. Kodanda Dhar Naik (Assistant Professor, CSE)</strong> and <strong className="text-white font-semibold">Mr. Kamalakanta Rout (Faculty, CSE)</strong>.
                </p>

                {/* Action Buttons */}
                <div className="flex flex-wrap items-center gap-4 pt-2">
                  
                  {/* Interactive Lab Button */}
                  <a
                    href="#threat-lab"
                    onClick={() => sounds.playClick(800, 0.04)}
                    className="px-6 py-3.5 rounded-xl solid-btn-blue font-mono font-bold text-xs uppercase tracking-wider flex items-center gap-2 shadow-solid-blue"
                  >
                    <Terminal className="w-4 h-4" />
                    <span>Launch Threat Lab</span>
                  </a>

                  {/* Download Coming Soon Button */}
                  <button
                    onClick={() => {
                      sounds.playClick(900, 0.04);
                      setDownloadModalOpen(true);
                    }}
                    className="px-5 py-3.5 rounded-xl bg-[#0F1626] border-2 border-slate-700 hover:border-amber-500 text-slate-200 hover:text-white font-mono text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition-all group"
                  >
                    <Clock className="w-4 h-4 text-amber-400 group-hover:scale-110 transition-transform" />
                    <span>Download App</span>
                    <span className="text-[10px] bg-amber-500/20 text-amber-400 border border-amber-500/40 px-1.5 py-0.5 rounded">
                      COMING SOON
                    </span>
                  </button>

                  {/* GitHub Repo Button */}
                  <a
                    href="https://github.com/cbagh0819-ctrl/VEX"
                    target="_blank"
                    rel="noopener noreferrer"
                    onClick={() => sounds.playClick(750, 0.03)}
                    className="px-4 py-3.5 rounded-xl bg-[#0B0F1A] border-2 border-[#1E293B] hover:border-blue-500 text-slate-300 hover:text-white font-mono text-xs font-bold transition-all flex items-center gap-2"
                  >
                    <GithubIcon className="w-4 h-4" />
                    <span className="hidden sm:inline">Source Code</span>
                  </a>
                </div>

                {/* Highlights pill row */}
                <div className="pt-4 border-t border-[#1E293B] grid grid-cols-3 gap-4 text-center sm:text-left">
                  <div>
                    <div className="text-2xl font-heading font-black text-white">500+</div>
                    <div className="text-[11px] font-mono text-slate-400 mt-0.5">PS09 Competitor Teams</div>
                  </div>
                  <div>
                    <div className="text-2xl font-heading font-black text-amber-400">3rd Prize</div>
                    <div className="text-[11px] font-mono text-slate-400 mt-0.5">Podium Trophy</div>
                  </div>
                  <div>
                    <div className="text-2xl font-heading font-black text-emerald-400">11.4 ms</div>
                    <div className="text-[11px] font-mono text-slate-400 mt-0.5">XAI Neural Latency</div>
                  </div>
                </div>

              </div>

              {/* Right Column: 3D Reactive Solid Shapes Canvas */}
              <div className="lg:col-span-6 relative">
                <Hero3D onInteract={() => sounds.playClick(600, 0.02)} />
              </div>

            </div>

          </div>
        </section>

        {/* SECTION 2: BPUT HACKATHON 2026 GLORY & STATISTICS */}
        <HackathonGlory onCelebrate={handleCelebrate} />

        {/* SECTION 3: AUTHENTIC HACKATHON JOURNEY & PHOTO STORY */}
        <HackathonJourney />

        {/* SECTION 4: REAL PROBLEM STATEMENT 09 (CYBERGUARD) */}
        <ProblemStatement09 />

        {/* SECTION 4: INTERACTIVE THREAT LAB & XAI BREAKDOWN */}
        <InteractiveThreatLab />

        {/* SECTION 5: VEX CORE ARCHITECTURE & PRODUCT CAPABILITIES */}
        <ProductFeatures />

        {/* SECTION 6: TEAM VEX LOG (MEMBERS & PHOTO PLACEHOLDERS) */}
        <TeamSection />

      </main>

      {/* Footer */}
      <Footer />

      {/* Coming Soon Download Modal */}
      <DownloadModal
        isOpen={downloadModalOpen}
        onClose={() => setDownloadModalOpen(false)}
      />

    </div>
  );
}

export default App;
