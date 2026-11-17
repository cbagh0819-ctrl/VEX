import React from 'react';
import { ShieldAlert, FileText, CheckCircle, ArrowRight, BrainCircuit, Binary, Lock, Eye } from 'lucide-react';
import { sounds } from '../utils/audio';
import { asset } from '../utils/assets';

export const ProblemStatement09: React.FC = () => {
  return (
    <section id="problem-statement" className="py-20 bg-[#06080E] border-b-2 border-[#1E293B] relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        {/* Header Ribbon */}
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-12 gap-6">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-md bg-blue-600/10 border border-blue-500/40 text-blue-400 font-mono text-xs font-bold uppercase tracking-wider mb-3">
              <FileText className="w-3.5 h-3.5" />
              <span>BPUT Hackathon 2026 • Official Problem Track</span>
            </div>
            <h2 className="text-3xl sm:text-4xl font-heading font-black text-white">
              Problem Statement 09: <br />
              <span className="text-blue-500">CYBERGUARD</span>
            </h2>
            <p className="text-slate-400 font-mono text-xs mt-1">
              "Trust Nothing, Explain Everything" — Sponsored by Odisha Knowledge Corporation Limited (OKCL)
            </p>
          </div>

          <div className="flex items-center gap-3 bg-[#0F1626] border-2 border-blue-600/40 px-5 py-3 rounded-xl shadow-solid-blue">
            <div className="w-3 h-3 rounded-full bg-emerald-500 animate-ping" />
            <div>
              <div className="text-[10px] font-mono uppercase text-slate-400">Assigned Domain</div>
              <div className="text-sm font-bold text-white">Cybersecurity &amp; Digital Trust AI</div>
            </div>
          </div>
        </div>

        {/* The Problem Statement Specification Card */}
        <div className="bg-[#0B0F1A] border-2 border-[#1E293B] rounded-2xl p-6 sm:p-8 shadow-2xl relative overflow-hidden mb-12">
          <div className="absolute top-0 right-0 w-48 h-48 bg-blue-600/10 rounded-bl-full pointer-events-none" />

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            
            {/* The Official Requirement */}
            <div className="lg:col-span-2 space-y-4">
              <h3 className="text-xl font-heading font-bold text-white flex items-center gap-2">
                <ShieldAlert className="w-5 h-5 text-amber-400" />
                The Real Problem Statement &amp; Challenge
              </h3>
              
              <div className="bg-[#080D18] border-l-4 border-amber-500 p-4 rounded-r-xl text-slate-200 text-sm leading-relaxed font-sans">
                <p className="font-semibold text-white mb-2">
                  "Develop an AI-powered cyber threat, phishing, and digital impersonation detection and response 
                  system built on a strict Zero-Trust philosophy. The system must not merely block threats as an opaque black box; 
                  it must explain in real-time why each transaction, network vector, or communication was classified as malicious."
                </p>
                <div className="text-xs font-mono text-slate-400 mt-2 flex items-center gap-3">
                  <span>Track ID: <strong>BPUT-2026-PS09</strong></span>
                  <span>•</span>
                  <span>Partner: <strong>OKCL (Odisha Knowledge Corporation)</strong></span>
                </div>
              </div>

              <div className="text-sm text-slate-300 leading-relaxed space-y-2 pt-2">
                <p>
                  Modern cyber adversaries exploit sophisticated zero-day techniques, executive brand impersonation, 
                  Unicode homoglyph attacks, and distributed log spoofing that bypass signature-based intrusion detection. 
                  Existing Security Information and Event Management (SIEM) systems suffer from high false-positive 
                  fatigue and offer zero explainability.
                </p>
                <p>
                  PS09 demanded an uncompromising dual capability: <strong>Extreme sub-second throughput</strong> combined with 
                  <strong> mathematical auditability (Explainable AI / XAI)</strong> to empower SOC analysts.
                </p>
              </div>
            </div>

            {/* The VEX Mission Matrix */}
            <div className="bg-[#0E1527] border-2 border-blue-500/30 p-5 rounded-xl shadow-solid-card flex flex-col justify-between">
              <div>
                <div className="text-xs font-mono text-blue-400 font-bold uppercase tracking-wider mb-2">
                  Key Evaluation Criteria
                </div>
                <div className="space-y-3 mt-3 text-xs">
                  <div className="flex items-start gap-2 text-slate-200">
                    <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                    <span><strong>Real-time Detection:</strong> Sub-50ms latency for streaming network &amp; host logs.</span>
                  </div>
                  <div className="flex items-start gap-2 text-slate-200">
                    <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                    <span><strong>Explainable AI (XAI):</strong> Detailed Shapley/Attention attribution per flagged event.</span>
                  </div>
                  <div className="flex items-start gap-2 text-slate-200">
                    <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                    <span><strong>Anti-Impersonation:</strong> Zero-shot visual &amp; lexical phishing classification.</span>
                  </div>
                  <div className="flex items-start gap-2 text-slate-200">
                    <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                    <span><strong>Zero-Trust Containment:</strong> Dynamic micro-segmentation rule generation.</span>
                  </div>
                </div>
              </div>

              <div className="mt-5 pt-3 border-t border-slate-700/60 text-[11px] font-mono text-slate-400">
                <span>VEX Scorecard: </span>
                <span className="text-emerald-400 font-bold">Top 3 in Odisha State</span>
              </div>
            </div>

          </div>

          {/* 3 Pillars of VEX's Solution */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-8 pt-6 border-t border-[#1E293B]">
            <div className="p-4 rounded-xl bg-[#090E1B] border border-blue-500/20">
              <div className="w-8 h-8 rounded-lg bg-blue-600/20 border border-blue-400 flex items-center justify-center text-blue-400 mb-3">
                <BrainCircuit className="w-4 h-4" />
              </div>
              <h4 className="text-sm font-bold text-white">1. Explainable Threat Neural Core</h4>
              <p className="text-xs text-slate-400 mt-1">
                Transformer-based log encoder extracting multi-head self-attention heatmaps to reveal anomalous payloads.
              </p>
            </div>

            <div className="p-4 rounded-xl bg-[#090E1B] border border-emerald-500/20">
              <div className="w-8 h-8 rounded-lg bg-emerald-600/20 border border-emerald-400 flex items-center justify-center text-emerald-400 mb-3">
                <Lock className="w-4 h-4" />
              </div>
              <h4 className="text-sm font-bold text-white">2. Zero-Trust Verification Bus</h4>
              <p className="text-xs text-slate-400 mt-1">
                Every API call, SSH session, and DNS query is treated as untrusted until validated through cryptographic proofs.
              </p>
            </div>

            <div className="p-4 rounded-xl bg-[#090E1B] border border-amber-500/20">
              <div className="w-8 h-8 rounded-lg bg-amber-600/20 border border-amber-400 flex items-center justify-center text-amber-400 mb-3">
                <Eye className="w-4 h-4" />
              </div>
              <h4 className="text-sm font-bold text-white">3. Visual &amp; Lexical Forensic UI</h4>
              <p className="text-xs text-slate-400 mt-1">
                Reactive 3D spatial interface letting SOC teams see the topology of attacks before they propagate.
              </p>
            </div>
          </div>

          {/* Official Problem Statement 09 Banner Photo */}
          <div className="mt-8 pt-6 border-t border-[#1E293B] grid grid-cols-1 lg:grid-cols-12 gap-6 items-center">
            <div className="lg:col-span-6 rounded-xl overflow-hidden border-2 border-blue-500/40 shadow-solid-blue bg-black relative group">
              <img 
                src={asset('/images/uploaded/team_problem09_banner.jpg')} 
                alt="Team VEX LOG standing in formals in front of the official Problem Statement 09 CYBERGUARD Banner holding Vex Log PS-9 placard"
                className="w-full h-auto object-cover group-hover:scale-102 transition-transform duration-300"
              />
              <div className="absolute bottom-0 inset-x-0 bg-gradient-to-t from-black/90 to-transparent p-3">
                <span className="text-[10px] font-mono font-bold text-blue-300 uppercase tracking-wider">
                  Official Verification: Team VEX LOG with BPUT Track 09 CYBERGUARD Banner
                </span>
              </div>
            </div>

            <div className="lg:col-span-6 space-y-3">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-md bg-emerald-500/10 border border-emerald-500/40 text-emerald-400 font-mono text-xs font-bold uppercase tracking-wider">
                <span>AUTHENTICATED PROBLEM STATEMENT</span>
              </div>
              <h4 className="text-lg sm:text-xl font-heading font-black text-white">
                "CYBERGUARD" — AI Powered Cyber Threat, Phishing &amp; Digital Impersonation Detection and Response System
              </h4>
              <p className="text-xs sm:text-sm text-slate-300 leading-relaxed font-normal">
                Held under the patronage of <strong className="text-white">BPUT</strong> and partner organizations like <strong className="text-white">OKCL</strong> and <strong className="text-white">Raajdhani Engineering College</strong>. Team VEX LOG demonstrated that zero-trust architectures can be both ultra-fast and human-understandable.
              </p>
            </div>
          </div>

        </div>

      </div>
    </section>
  );
};
