import React from 'react';
import { Shield, Award, ExternalLink, GraduationCap, Building2 } from 'lucide-react';
import { GithubIcon } from './Icons';
import { sounds } from '../utils/audio';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-[#04060A] border-t-2 border-[#1E293B] py-16 text-slate-400 font-sans relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-10 pb-12 border-b border-[#1E293B]">
          
          {/* Col 1: VEX App info */}
          <div className="space-y-4">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-lg bg-blue-600 border border-blue-400 flex items-center justify-center text-white shadow-solid-blue">
                <Shield className="w-4 h-4" />
              </div>
              <span className="font-heading font-black text-xl text-white tracking-wider">VEX</span>
              <span className="text-[10px] font-mono text-amber-400 border border-amber-500/30 px-1.5 py-0.5 rounded bg-amber-500/10">
                TEAM VEX LOG
              </span>
            </div>
            
            <p className="text-xs text-slate-400 leading-relaxed">
              AI-Powered Cyber Threat, Phishing &amp; Digital Impersonation Detection and Response System. 
              Built on a strict Zero-Trust philosophy with sub-second Explainable AI (XAI) attribution.
            </p>

            <div className="text-[11px] font-mono text-slate-400 flex items-center gap-1.5">
              <Building2 className="w-3.5 h-3.5 text-amber-400" />
              <span>PMEC, Berhampur (Odisha)</span>
            </div>

            <div className="text-[11px] font-mono text-slate-500">
              Developed for BPUT Hackathon 2026 • Problem Statement 09
            </div>
          </div>

          {/* Col 2: Hackathon Details */}
          <div className="space-y-3">
            <h4 className="text-xs font-mono uppercase tracking-wider text-white font-bold flex items-center gap-1.5">
              <Award className="w-4 h-4 text-amber-400" />
              Hackathon Honors
            </h4>
            <ul className="space-y-2 text-xs">
              <li className="text-amber-400 font-semibold">• 3rd Prize (2nd Runner-Up) Winner</li>
              <li>• BPUT Hackathon 2026 Grand Finale</li>
              <li>• Venue: BPUT Main Campus, Rourkela</li>
              <li>• Track: PS09 CYBERGUARD</li>
              <li>• Partner: Odisha Knowledge Corporation (OKCL)</li>
              <li>• 500+ Competing Track Teams</li>
            </ul>
          </div>

          {/* Col 3: Team & Faculty Mentors */}
          <div className="space-y-3">
            <h4 className="text-xs font-mono uppercase tracking-wider text-white font-bold">
              Team &amp; Mentorship
            </h4>
            
            <div className="pb-2.5 mb-2.5 border-b border-slate-800 space-y-2">
              <div className="text-[10px] font-mono uppercase text-amber-400 font-bold flex items-center gap-1">
                <GraduationCap className="w-3.5 h-3.5" />
                Faculty Mentors &amp; Guidance:
              </div>
              
              <div>
                <div className="text-xs font-bold text-white">Mr. Kodanda Dhar Naik (K.D. Naik)</div>
                <div className="text-[10px] text-slate-400">Assistant Professor, CSE, PMEC</div>
              </div>

              <div>
                <div className="text-xs font-bold text-white">Mr. Kamalakanta Rout</div>
                <div className="text-[10px] text-slate-400">Faculty Member, CSE, PMEC</div>
              </div>
            </div>

            <div className="text-[10px] font-mono uppercase text-blue-400 font-bold">
              Student Developers:
            </div>
            <ul className="space-y-1 text-xs text-slate-300 font-mono">
              <li>1. Ankit Mandi <span className="text-blue-400">(Lead)</span></li>
              <li>2. Chandan Bagh</li>
              <li>3. Deepak Dash</li>
              <li>4. Omm Prakash Mallik</li>
              <li>5. Padmalochan Behera</li>
            </ul>
          </div>

          {/* Col 4: Quick Links */}
          <div className="space-y-3">
            <h4 className="text-xs font-mono uppercase tracking-wider text-white font-bold">
              Repositories &amp; Links
            </h4>
            <div className="space-y-2 text-xs">
              <a
                href="https://github.com/cbagh0819-ctrl/VEX"
                target="_blank"
                rel="noopener noreferrer"
                onClick={() => sounds.playClick(700, 0.03)}
                className="flex items-center gap-1.5 text-slate-300 hover:text-blue-400 transition-colors"
              >
                <GithubIcon className="w-3.5 h-3.5" />
                <span>GitHub Source Repository</span>
                <ExternalLink className="w-3 h-3 text-slate-500" />
              </a>

              <a
                href="https://pmec.ac.in/"
                target="_blank"
                rel="noopener noreferrer"
                onClick={() => sounds.playClick(700, 0.03)}
                className="flex items-center gap-1.5 text-amber-300 hover:text-amber-200 font-medium transition-colors"
              >
                <Building2 className="w-3.5 h-3.5" />
                <span>PMEC Berhampur (College Portal)</span>
                <ExternalLink className="w-3 h-3 text-slate-500" />
              </a>

              <a
                href="https://bput.ac.in/"
                target="_blank"
                rel="noopener noreferrer"
                onClick={() => sounds.playClick(700, 0.03)}
                className="flex items-center gap-1.5 text-slate-300 hover:text-amber-400 transition-colors"
              >
                <span>BPUT University Portal</span>
                <ExternalLink className="w-3 h-3 text-slate-500" />
              </a>

              <a
                href="https://okcl.org/"
                target="_blank"
                rel="noopener noreferrer"
                onClick={() => sounds.playClick(700, 0.03)}
                className="flex items-center gap-1.5 text-slate-300 hover:text-emerald-400 transition-colors"
              >
                <span>OKCL Official Website</span>
                <ExternalLink className="w-3 h-3 text-slate-500" />
              </a>
            </div>
          </div>

        </div>

        {/* Bottom bar */}
        <div className="pt-8 flex flex-col sm:flex-row items-center justify-between text-xs font-mono text-slate-500 gap-4">
          <div>
            © 2026 Team VEX LOG • Parala Maharaja Engineering College (PMEC), Berhampur.
          </div>
          <div className="flex items-center gap-2">
            <span>Built for BPUT Hackathon 2026</span>
          </div>
        </div>

      </div>
    </footer>
  );
};
