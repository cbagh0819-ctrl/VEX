import React from 'react';
import { Award, Trophy, Users, ShieldAlert, Cpu, Flame, Target, CheckCircle2, ChevronRight, Zap } from 'lucide-react';
import { sounds } from '../utils/audio';
import { asset } from '../utils/assets';

interface HackathonGloryProps {
  onCelebrate: () => void;
}

export const HackathonGlory: React.FC<HackathonGloryProps> = ({ onCelebrate }) => {
  return (
    <section id="hackathon-arena" className="py-20 bg-[#080B13] border-b-2 border-[#1E293B] relative overflow-hidden">
      {/* Background Grid Accent */}
      <div className="absolute inset-0 bg-cyber-grid opacity-30 pointer-events-none" />
      
      {/* Solid Amber Light Cone */}
      <div className="absolute top-0 right-1/4 w-96 h-96 bg-amber-500/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 left-10 w-80 h-80 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        {/* Section Header */}
        <div className="flex flex-col items-center text-center mb-14">
          <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-amber-500/10 border-2 border-amber-500/40 text-amber-400 font-mono text-xs font-bold uppercase tracking-wider mb-4 shadow-solid-gold">
            <Trophy className="w-4 h-4 text-amber-400 fill-amber-400/20" />
            <span>Grand Finale Triumph • BPUT Main Campus, Rourkela</span>
          </div>

          <h2 className="text-3xl sm:text-5xl font-heading font-black text-white tracking-tight max-w-4xl">
            Winning in the Crucible: <br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 via-yellow-300 to-amber-500">
              3rd Prize at BPUT Hackathon 2026
            </span>
          </h2>

          <p className="mt-4 text-slate-300 max-w-3xl text-base sm:text-lg leading-relaxed">
            Out of <strong className="text-white font-semibold">500+ registered teams</strong> across 
            Odisha’s premier technical institutions, Team <strong className="text-amber-400">VEX LOG</strong> clinched the 
            prestigious <strong className="text-white font-semibold">3rd Prize (2nd Runner-Up)</strong> podium finish 
            at the state-level flagship hackathon.
          </p>

          <button
            onClick={() => {
              sounds.playFanfare();
              onCelebrate();
            }}
            className="mt-6 px-6 py-2.5 rounded-xl solid-btn-gold font-mono font-bold text-sm tracking-wider uppercase flex items-center gap-2 shadow-solid-gold cursor-pointer"
          >
            <Award className="w-5 h-5 fill-white" />
            <span>Trigger Victory Fanfare</span>
          </button>
        </div>

        {/* 4 Solid Stat Blocks */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
          
          {/* Stat 1 */}
          <div className="bg-[#0E1424] border-2 border-amber-500/50 p-6 rounded-2xl shadow-solid-gold relative overflow-hidden group hover:-translate-y-1 transition-transform">
            <div className="w-12 h-12 rounded-xl bg-amber-500/20 border-2 border-amber-400 flex items-center justify-center text-amber-400 mb-4">
              <Trophy className="w-6 h-6" />
            </div>
            <div className="text-4xl font-heading font-black text-white">3rd Prize</div>
            <div className="text-xs font-mono text-amber-400 font-bold uppercase mt-1">Statewide Podium Rank</div>
            <p className="text-xs text-slate-400 mt-2 leading-normal">
              Awarded in the flagship Grand Finale showdown at BPUT Main Campus, Rourkela.
            </p>
          </div>

          {/* Stat 2 */}
          <div className="bg-[#0E1424] border-2 border-blue-500/50 p-6 rounded-2xl shadow-solid-blue relative overflow-hidden group hover:-translate-y-1 transition-transform">
            <div className="w-12 h-12 rounded-xl bg-blue-500/20 border-2 border-blue-400 flex items-center justify-center text-blue-400 mb-4">
              <Users className="w-6 h-6" />
            </div>
            <div className="text-4xl font-heading font-black text-white">500+</div>
            <div className="text-xs font-mono text-blue-400 font-bold uppercase mt-1">Teams in Track PS09</div>
            <p className="text-xs text-slate-400 mt-2 leading-normal">
              One of the most heavily contested problem statements in the entire hackathon.
            </p>
          </div>

          {/* Stat 3 */}
          <div className="bg-[#0E1424] border-2 border-emerald-500/50 p-6 rounded-2xl shadow-solid-emerald relative overflow-hidden group hover:-translate-y-1 transition-transform">
            <div className="w-12 h-12 rounded-xl bg-emerald-500/20 border-2 border-emerald-400 flex items-center justify-center text-emerald-400 mb-4">
              <Flame className="w-6 h-6" />
            </div>
            <div className="text-4xl font-heading font-black text-white">36 Hours</div>
            <div className="text-xs font-mono text-emerald-400 font-bold uppercase mt-1">Continuous Sprint</div>
            <p className="text-xs text-slate-400 mt-2 leading-normal">
              Zero-sleep, live deployment, defending against live adversarial threat streams under jury scrutiny.
            </p>
          </div>

          {/* Stat 4 */}
          <div className="bg-[#0E1424] border-2 border-purple-500/50 p-6 rounded-2xl shadow-solid-card relative overflow-hidden group hover:-translate-y-1 transition-transform">
            <div className="w-12 h-12 rounded-xl bg-purple-500/20 border-2 border-purple-400 flex items-center justify-center text-purple-400 mb-4">
              <ShieldAlert className="w-6 h-6" />
            </div>
            <div className="text-4xl font-heading font-black text-white">&lt; 3%</div>
            <div className="text-xs font-mono text-purple-400 font-bold uppercase mt-1">Acceptance Rate</div>
            <p className="text-xs text-slate-400 mt-2 leading-normal">
              Rigorous virtual mid-evaluations screened out 97% of teams before on-site Grand Finale.
            </p>
          </div>

        </div>

        {/* OFFICIAL STAGE VICTORY & GRAND FINALE EVIDENCE SHOWCASE */}
        <div className="mb-16 bg-[#0B101D] border-2 border-amber-500/60 rounded-2xl p-6 sm:p-8 shadow-solid-gold relative overflow-hidden">
          <div className="absolute top-0 right-0 w-80 h-80 bg-amber-500/10 rounded-full blur-3xl pointer-events-none" />
          
          <div className="flex flex-wrap items-center justify-between gap-4 mb-6 pb-4 border-b border-[#1E293B]">
            <div className="flex items-center gap-2.5">
              <span className="w-3 h-3 rounded-full bg-amber-400 animate-pulse" />
              <span className="font-mono text-xs font-bold text-amber-400 uppercase tracking-widest">
                OFFICIAL GRAND FINALE STAGE MOMENT • BPUT HACKATHON 2026
              </span>
            </div>
            <div className="font-mono text-xs text-slate-400">
              Team ID: <strong className="text-white">BH26PS09T067</strong> • Category: <strong className="text-amber-400">2nd Runner-Up (3rd Prize)</strong>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
            {/* Stage Victory Photo with LED Screen */}
            <div className="lg:col-span-7 relative group rounded-xl overflow-hidden border-2 border-[#1E293B] shadow-2xl bg-black">
              <img 
                src={asset('/images/uploaded/stage_led_victory.jpg')} 
                alt="BPUT Hackathon 2026 Grand Finale Winner Stage Victory with Team VEX LOG and Mentor Mr. Kodanda Dhar Naik"
                className="w-full h-auto object-cover group-hover:scale-102 transition-transform duration-300"
              />
              <div className="absolute bottom-0 inset-x-0 bg-gradient-to-t from-black/95 via-black/60 to-transparent p-4">
                <div className="text-amber-400 font-mono text-[11px] font-bold tracking-wide uppercase">
                  Grand Finale Winner Announcement • Grand Stage
                </div>
                <div className="text-xs text-slate-200 mt-0.5">
                  Giant LED display: <strong className="text-white">"TEAM NAME: VEX LOG | P.M.E.C. BERHAMPUR"</strong> receiving the 3rd Prize Trophy and ₹10,000 Cheque alongside Faculty Mentor <strong className="text-white">Mr. Kodanda Dhar Naik</strong>.
                </div>
              </div>
            </div>

            {/* Auditorium Scale & Facts */}
            <div className="lg:col-span-5 space-y-4">
              <div className="rounded-xl overflow-hidden border-2 border-[#1E293B] bg-black relative group">
                <img 
                  src={asset('/images/uploaded/grand_auditorium_crowd.jpg')} 
                  alt="Packed BPUT Grand Auditorium with over 500 participants and dignitaries"
                  className="w-full h-44 object-cover group-hover:scale-105 transition-transform duration-300"
                />
                <div className="absolute bottom-0 inset-x-0 bg-gradient-to-t from-black/90 to-transparent p-2.5">
                  <span className="text-[10px] font-mono font-bold text-blue-300 uppercase tracking-wider">
                    500+ Participants Standing in BPUT Grand Auditorium
                  </span>
                </div>
              </div>

              <div className="bg-[#0F172A] border border-amber-500/30 p-4 rounded-xl space-y-2">
                <div className="flex items-center gap-2 text-amber-400 font-mono text-xs font-bold">
                  <Award className="w-4 h-4" />
                  <span>Verified Hackathon Victory</span>
                </div>
                <p className="text-xs text-slate-300 leading-relaxed font-normal">
                  Out of hundreds of engineering teams across Odisha, Team <strong className="text-white">VEX LOG</strong> from <strong className="text-amber-300">Parala Maharaja Engineering College (PMEC)</strong> rose through 36 continuous hours of adversarial evaluations to capture the podium finish for Problem Statement 09.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Why BPUT Hackathon Is So Difficult & Why Winning is Massive */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 items-stretch">
          
          {/* Left Column: The Scale & Severity */}
          <div className="bg-[#0B0F19] border-2 border-[#1E293B] p-8 rounded-2xl shadow-2xl flex flex-col justify-between">
            <div>
              <div className="flex items-center gap-2 text-xs font-mono text-red-400 font-bold uppercase tracking-wider mb-3">
                <Target className="w-4 h-4" />
                <span>The Engineering Challenge</span>
              </div>
              <h3 className="text-2xl sm:text-3xl font-heading font-black text-white">
                How Intense Was BPUT Hackathon 2026?
              </h3>
              <p className="mt-3 text-slate-300 text-sm leading-relaxed">
                Organized by the <strong className="text-white">Biju Patnaik University of Technology (BPUT)</strong>, 
                this hackathon serves as Odisha's flagship proving ground for future cyber defense, hardware innovation, 
                and enterprise AI.
              </p>

              <div className="mt-6 space-y-4">
                <div className="flex items-start gap-3">
                  <div className="w-6 h-6 rounded-md bg-red-500/20 border border-red-500 text-red-400 flex items-center justify-center font-bold text-xs mt-0.5 shrink-0">
                    1
                  </div>
                  <div>
                    <h4 className="text-sm font-semibold text-white">Statewide Collegiate Convergence</h4>
                    <p className="text-xs text-slate-400 mt-1">
                      Teams represented top government engineering universities, autonomous colleges, and research labs 
                      from across Odisha, submitting hardened algorithmic prototypes.
                    </p>
                  </div>
                </div>

                <div className="flex items-start gap-3">
                  <div className="w-6 h-6 rounded-md bg-amber-500/20 border border-amber-500 text-amber-400 flex items-center justify-center font-bold text-xs mt-0.5 shrink-0">
                    2
                  </div>
                  <div>
                    <h4 className="text-sm font-semibold text-white">Adversarial Live-Firing Evaluations</h4>
                    <p className="text-xs text-slate-400 mt-1">
                      Jurors did not just accept slide decks. They injected zero-day attack payloads, spoofed TLS handshakes, 
                      and malformed log files into running applications to verify fault tolerance in real time.
                    </p>
                  </div>
                </div>

                <div className="flex items-start gap-3">
                  <div className="w-6 h-6 rounded-md bg-blue-500/20 border border-blue-500 text-blue-400 flex items-center justify-center font-bold text-xs mt-0.5 shrink-0">
                    3
                  </div>
                  <div>
                    <h4 className="text-sm font-semibold text-white">Strict Enterprise Sponsor Benchmarks</h4>
                    <p className="text-xs text-slate-400 mt-1">
                      Backed by organizations like Odisha Knowledge Corporation Limited (OKCL), STPI, Cognizant, and 
                      Arduino, every architecture had to adhere to production standards, latency limits (&lt;50ms), and 
                      provable audit trails.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div className="mt-8 pt-4 border-t border-[#1E293B] flex items-center justify-between text-xs font-mono text-slate-400">
              <span>Grand Finale: Nov 12–13, 2026</span>
              <span className="text-amber-400 font-semibold">BPUT Campus, Rourkela</span>
            </div>
          </div>

          {/* Right Column: Why Winning 3rd Prize is Great */}
          <div className="bg-[#0B0F19] border-2 border-amber-500/40 p-8 rounded-2xl shadow-solid-gold flex flex-col justify-between relative overflow-hidden">
            <div className="absolute top-0 right-0 w-32 h-32 bg-amber-500/10 rounded-bl-full pointer-events-none" />

            <div>
              <div className="flex items-center gap-2 text-xs font-mono text-amber-400 font-bold uppercase tracking-wider mb-3">
                <Award className="w-4 h-4" />
                <span>The Validation</span>
              </div>
              <h3 className="text-2xl sm:text-3xl font-heading font-black text-white">
                Why Team VEX LOG’s Victory Matters
              </h3>
              <p className="mt-3 text-slate-300 text-sm leading-relaxed">
                In cybersecurity, most solutions are <strong className="text-white">"Black Box"</strong> detection engines 
                that throw false-positive alarms without giving security teams actionable reasons.
              </p>

              <div className="mt-6 space-y-4">
                <div className="p-4 rounded-xl bg-[#131B2E] border border-blue-500/30 flex items-start gap-3">
                  <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0 mt-0.5" />
                  <div>
                    <h4 className="text-sm font-bold text-white">Zero Black-Boxes: Real Explainable AI</h4>
                    <p className="text-xs text-slate-300 mt-1">
                      VEX proved that deep neural models can generate token-level Shapley explanations and MITRE ATT&CK 
                      attribution in under 12 milliseconds per log packet.
                    </p>
                  </div>
                </div>

                <div className="p-4 rounded-xl bg-[#131B2E] border border-amber-500/30 flex items-start gap-3">
                  <CheckCircle2 className="w-5 h-5 text-amber-400 shrink-0 mt-0.5" />
                  <div>
                    <h4 className="text-sm font-bold text-white">Jury Commendation on Architecture</h4>
                    <p className="text-xs text-slate-300 mt-1">
                      The evaluation panel recognized Team VEX LOG for combining high-throughput Rust/Python forensic pipelines 
                      with an intuitive, reactive 3D situational awareness canvas.
                    </p>
                  </div>
                </div>

                <div className="p-4 rounded-xl bg-[#131B2E] border border-emerald-500/30 flex items-start gap-3">
                  <CheckCircle2 className="w-5 h-5 text-blue-400 shrink-0 mt-0.5" />
                  <div>
                    <h4 className="text-sm font-bold text-white">Ready for Enterprise Pilot</h4>
                    <p className="text-xs text-slate-300 mt-1">
                      Securing 3rd Prize on this massive stage validates VEX not just as a student project, but as an 
                      industrial-grade cyber intelligence suite ready for real-world deployment.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div className="mt-8 pt-4 border-t border-[#1E293B] flex items-center justify-between text-xs font-mono">
              <span className="text-slate-400">Jury Decision: Unanimous Podium</span>
              <span className="text-emerald-400 font-bold">VERIFIED 3RD PRIZE</span>
            </div>
          </div>

        </div>

      </div>
    </section>
  );
};
