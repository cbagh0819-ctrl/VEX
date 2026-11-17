import React from 'react';
import { Brain, Cpu, ShieldCheck, Zap, Network, Activity, FileSearch, Lock, Layers } from 'lucide-react';
import { sounds } from '../utils/audio';

const FEATURES = [
  {
    title: 'Explainable AI (XAI) Engine',
    subtitle: 'Zero Black Boxes',
    description: 'Every alert comes with human-auditable Shapley value weights and token-level attention maps so SOC analysts know exactly why an anomaly was flagged in under 12ms.',
    badge: 'XAI Core',
    color: 'blue',
    icon: Brain,
    solidBg: 'bg-blue-600',
    borderColor: 'border-blue-500',
    shadowColor: 'shadow-solid-blue'
  },
  {
    title: 'High-Throughput Log Pipeline',
    subtitle: '100,000+ EPS Ingestion',
    description: 'Engineered in Rust and Python with SIMD vectorization to parse, normalize, and tokenize Syslog, CloudTrail, VPC Flow, and auditd streams with zero buffer overrun.',
    badge: 'Performance',
    color: 'emerald',
    icon: Zap,
    solidBg: 'bg-emerald-600',
    borderColor: 'border-emerald-500',
    shadowColor: 'shadow-solid-emerald'
  },
  {
    title: 'Anti-Impersonation & Phishing Decimator',
    subtitle: 'Unicode & Homoglyph Defense',
    description: 'Combines character-level transformer decoders and visual neural perceptual hashing to detect spoofed domains, counterfeit logos, and deceptive executive credentials.',
    badge: 'Zero Trust',
    color: 'amber',
    icon: ShieldCheck,
    solidBg: 'bg-amber-600',
    borderColor: 'border-amber-500',
    shadowColor: 'shadow-solid-gold'
  },
  {
    title: 'Autonomous Policy Synthesizer',
    subtitle: 'Dynamic Micro-Segmentation',
    description: 'Translates anomalous threat classifications directly into kernel-level iptables rules, eBPF filters, and identity revocation webhooks without manual intervention.',
    badge: 'Automated Containment',
    color: 'purple',
    icon: Lock,
    solidBg: 'bg-purple-600',
    borderColor: 'border-purple-500',
    shadowColor: 'shadow-solid-card'
  },
  {
    title: 'MITRE ATT&CK & D3FEND Mapping',
    subtitle: 'Enterprise Killchain Context',
    description: 'Correlates isolated log lines into multi-stage attack campaigns, mapping tactics from initial reconnaissance through lateral movement to data exfiltration.',
    badge: 'Threat Matrix',
    color: 'cyan',
    icon: Network,
    solidBg: 'bg-cyan-600',
    borderColor: 'border-cyan-500',
    shadowColor: 'shadow-solid-blue'
  },
  {
    title: '3D Spatial Threat Topology',
    subtitle: 'Interactive Situational Canvas',
    description: 'Provides SOC commanders with real-time 3D reactive geometric projections of enterprise subnets, visualizing threat propagation vectors and blast containment boundaries.',
    badge: '3D Reactive UI',
    color: 'blue',
    icon: Layers,
    solidBg: 'bg-blue-700',
    borderColor: 'border-blue-400',
    shadowColor: 'shadow-solid-blue'
  }
];

export const ProductFeatures: React.FC = () => {
  return (
    <section id="capabilities" className="py-20 bg-[#080B13] border-b-2 border-[#1E293B] relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        {/* Header */}
        <div className="flex flex-col items-center text-center mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-md bg-blue-600/10 border border-blue-500/40 text-blue-400 font-mono text-xs font-bold uppercase tracking-wider mb-3">
            <Cpu className="w-3.5 h-3.5" />
            <span>Architecture &amp; Capabilities</span>
          </div>

          <h2 className="text-3xl sm:text-5xl font-heading font-black text-white tracking-tight max-w-3xl">
            Built for Modern Cyber Defense. <br />
            <span className="text-blue-500">Engineered with Mathematical Rigor.</span>
          </h2>

          <p className="mt-4 text-slate-300 max-w-2xl text-base leading-relaxed">
            Everything anyone needs to know about VEX: how our hybrid neural architecture ingests, explains, 
            and eliminates cyber threats in real-time.
          </p>
        </div>

        {/* Feature Cards Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {FEATURES.map((feature, idx) => {
            const IconComponent = feature.icon;
            return (
              <div
                key={idx}
                onMouseEnter={() => sounds.playClick(850 + idx * 30, 0.02)}
                className={`bg-[#0C111E] border-2 ${feature.borderColor} p-6 rounded-2xl ${feature.shadowColor} transition-all duration-200 hover:-translate-y-2 flex flex-col justify-between group`}
              >
                <div>
                  <div className="flex items-center justify-between mb-4">
                    <div className={`w-12 h-12 rounded-xl ${feature.solidBg} border-2 border-white/20 flex items-center justify-center text-white shadow-md group-hover:rotate-6 transition-transform`}>
                      <IconComponent className="w-6 h-6" />
                    </div>
                    <span className="text-[10px] font-mono font-bold uppercase tracking-wider px-2 py-1 rounded bg-[#162035] text-slate-300 border border-slate-700">
                      {feature.badge}
                    </span>
                  </div>

                  <div className="text-xs font-mono text-blue-400 uppercase font-semibold">
                    {feature.subtitle}
                  </div>
                  <h3 className="text-xl font-heading font-black text-white mt-1">
                    {feature.title}
                  </h3>
                  <p className="mt-3 text-slate-300 text-sm leading-relaxed">
                    {feature.description}
                  </p>
                </div>

                <div className="mt-6 pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs font-mono text-slate-400">
                  <span>Latency: &lt;15ms</span>
                  <span className="text-white font-semibold group-hover:text-blue-400 transition-colors">
                    PS09 Compliant →
                  </span>
                </div>
              </div>
            );
          })}
        </div>

      </div>
    </section>
  );
};
