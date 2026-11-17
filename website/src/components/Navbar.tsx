import React, { useState } from 'react';
import { Shield, Volume2, VolumeX, Award, Terminal, Users, Sparkles, ExternalLink, Camera } from 'lucide-react';
import { GithubIcon } from './Icons';
import { sounds } from '../utils/audio';

interface NavbarProps {
  onOpenDownload: () => void;
  onOpenConfetti: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onOpenDownload, onOpenConfetti }) => {
  const [audioEnabled, setAudioEnabled] = useState(true);

  const toggleAudio = () => {
    const next = !audioEnabled;
    setAudioEnabled(next);
    sounds.enabled = next;
    if (next) sounds.playClick(900, 0.05);
  };

  return (
    <nav className="sticky top-0 z-50 w-full bg-[#06080E]/90 backdrop-blur-md border-b-2 border-[#1E293B]">
      {/* Top Banner: BPUT Hackathon 2026 Winner Ribbon */}
      <div className="bg-gradient-to-r from-amber-600 via-yellow-500 to-amber-600 px-4 py-1.5 text-center text-xs font-mono font-bold text-black tracking-wide flex items-center justify-center gap-2 shadow-inner">
        <Award className="w-4 h-4 fill-black" />
        <span>PROUD 3RD PRIZE WINNER • BPUT HACKATHON 2026 (PS09 CYBERGUARD) • GRAND FINALE ROURKELA</span>
        <button
          onClick={() => {
            sounds.playFanfare();
            onOpenConfetti();
          }}
          className="ml-2 px-2 py-0.5 rounded bg-black text-amber-400 text-[10px] font-bold uppercase tracking-wider hover:bg-slate-900 transition-colors shadow-sm"
        >
          Celebrate 🎉
        </button>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo & Team Brand */}
          <div className="flex items-center gap-3">
            <a 
              href="#" 
              onClick={() => sounds.playClick(700, 0.04)}
              className="flex items-center gap-2.5 group"
            >
              <div className="w-10 h-10 rounded-xl bg-blue-600 border-2 border-blue-400 flex items-center justify-center shadow-solid-blue group-hover:rotate-6 transition-transform">
                <Shield className="w-5 h-5 text-white" />
              </div>
              <div className="flex flex-col">
                <div className="flex items-center gap-1.5">
                  <span className="font-heading font-black text-xl text-white tracking-wider">VEX</span>
                  <span className="bg-[#1E293B] text-blue-400 text-[10px] font-mono px-1.5 py-0.5 rounded border border-blue-500/30 font-semibold">
                    v0.9.8-rc
                  </span>
                </div>
                <span className="text-[10px] font-mono text-slate-400 font-semibold -mt-1 tracking-tight">
                  BY TEAM <span className="text-amber-400">VEX LOG</span>
                </span>
              </div>
            </a>
          </div>

          {/* Desktop Nav Items */}
          <div className="hidden lg:flex items-center gap-6 text-sm font-medium text-slate-300">
            <a
              href="#problem-statement"
              onClick={() => sounds.playClick(600, 0.03)}
              className="hover:text-blue-400 font-mono text-xs uppercase tracking-wider transition-colors"
            >
              Problem 09
            </a>
            <a
              href="#hackathon-arena"
              onClick={() => sounds.playClick(600, 0.03)}
              className="hover:text-amber-400 font-mono text-xs uppercase tracking-wider transition-colors flex items-center gap-1 text-amber-300/90"
            >
              <Award className="w-3.5 h-3.5" />
              Hackathon Arena
            </a>
            <a
              href="#hackathon-journey"
              onClick={() => sounds.playClick(600, 0.03)}
              className="hover:text-amber-400 font-mono text-xs uppercase tracking-wider transition-colors flex items-center gap-1 text-amber-300 font-semibold"
            >
              <Camera className="w-3.5 h-3.5 text-amber-400" />
              Our Journey
            </a>
            <a
              href="#threat-lab"
              onClick={() => sounds.playClick(600, 0.03)}
              className="hover:text-emerald-400 font-mono text-xs uppercase tracking-wider transition-colors flex items-center gap-1"
            >
              <Terminal className="w-3.5 h-3.5" />
              Live Threat Lab
            </a>
            <a
              href="#capabilities"
              onClick={() => sounds.playClick(600, 0.03)}
              className="hover:text-blue-400 font-mono text-xs uppercase tracking-wider transition-colors"
            >
              Capabilities
            </a>
            <a
              href="#team-section"
              onClick={() => sounds.playClick(600, 0.03)}
              className="hover:text-blue-400 font-mono text-xs uppercase tracking-wider transition-colors flex items-center gap-1"
            >
              <Users className="w-3.5 h-3.5" />
              Team VEX LOG
            </a>
          </div>

          {/* Right Action Buttons */}
          <div className="flex items-center gap-3">
            {/* Audio Toggle */}
            <button
              onClick={toggleAudio}
              className="p-2 rounded-lg bg-[#0F172A] border border-[#1E293B] text-slate-400 hover:text-white transition-colors"
              title={audioEnabled ? 'Mute Cyber Audio' : 'Enable Cyber Audio'}
            >
              {audioEnabled ? <Volume2 className="w-4 h-4 text-emerald-400" /> : <VolumeX className="w-4 h-4 text-slate-500" />}
            </button>

            {/* GitHub Repo Button */}
            <a
              href="https://github.com/cbagh0819-ctrl/VEX"
              target="_blank"
              rel="noopener noreferrer"
              onClick={() => sounds.playClick(800, 0.04)}
              className="flex items-center gap-2 px-3 py-2 rounded-xl bg-[#0F172A] border-2 border-[#1E293B] text-slate-200 hover:text-white hover:border-blue-500 transition-all font-mono text-xs font-semibold"
              title="View Source on GitHub"
            >
              <GithubIcon className="w-4 h-4 text-white" />
              <span className="hidden sm:inline">GitHub</span>
              <ExternalLink className="w-3 h-3 text-slate-400" />
            </a>

            {/* Download Coming Soon CTA */}
            <button
              onClick={() => {
                sounds.playClick(900, 0.05);
                onOpenDownload();
              }}
              className="flex items-center gap-1.5 px-4 py-2 rounded-xl solid-btn-blue text-xs font-mono font-bold tracking-wider uppercase cursor-pointer"
            >
              <Sparkles className="w-3.5 h-3.5" />
              <span>Get App</span>
              <span className="bg-blue-900/80 text-blue-200 text-[10px] px-1.5 py-0.2 rounded border border-blue-300/40">
                SOON
              </span>
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};
