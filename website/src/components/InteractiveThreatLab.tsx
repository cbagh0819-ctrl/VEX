import React, { useState } from 'react';
import { Terminal, Play, ShieldAlert, ShieldCheck, Cpu, AlertTriangle, ArrowRight, RefreshCw, Layers } from 'lucide-react';
import { sounds } from '../utils/audio';

interface AttackScenario {
  id: string;
  name: string;
  category: string;
  severity: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'SAFE';
  confidence: number;
  sampleLog: string;
  explanation: {
    rootCause: string;
    anomalyFeatures: { label: string; impact: number }[];
    mitreTechnique: string;
    zeroTrustVerdict: string;
    containmentAction: string;
  };
}

const SCENARIOS: AttackScenario[] = [
  {
    id: 'phish',
    name: 'Executive Homoglyph Impersonation',
    category: 'Digital Impersonation',
    severity: 'CRITICAL',
    confidence: 99.4,
    sampleLog: `POST /auth/login HTTP/2.0
Host: auth.micr0s0ft-security-corp.portal-okcl.xyz
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64) AppleWebKit/537.36
Payload: { "u": "vp_finance@bput.ac.in", "t": "SSO_TOKEN_EXCHANGE" }
Entropy: 7.842 | TLS Fingerprint: JA3 6734f2d9e8c4...`,
    explanation: {
      rootCause: 'Cyrillic homoglyph substitution in FQDN ("0" replacing "o") and unverified external registrar targeting campus faculty credentials.',
      anomalyFeatures: [
        { label: 'Homoglyph Character Substitution', impact: 98 },
        { label: 'Domain Age < 48 Hours', impact: 94 },
        { label: 'Forged OAuth Relay Header', impact: 91 },
        { label: 'High Shannon Payload Entropy', impact: 86 }
      ],
      mitreTechnique: 'T1566.002 (Phishing: Spearphishing Link) / T1583.001 (Acquire Infrastructure)',
      zeroTrustVerdict: 'ACCESS DENIED • ZERO IDENTITY TRUST',
      containmentAction: 'Revoked downstream token, pushed global DNS sinkhole entry via VEX agent.'
    }
  },
  {
    id: 'kerberoast',
    name: 'Kerberos TGS Ticket Downgrade',
    category: 'Lateral Movement',
    severity: 'HIGH',
    confidence: 96.2,
    sampleLog: `EVENT_ID: 4769 | TargetService: MSSQLSvc/sql01.bput.internal:1433
TicketOptions: 0x40810000 | TicketEncryptionType: 0x17 (RC4-HMAC-MD5)
ClientAddress: ::ffff:192.168.44.118 | Status: 0x0
Rate: 142 ticket requests / 3.4 seconds from standard workstation node.`,
    explanation: {
      rootCause: 'Abnormal burst of Kerberos Service Ticket requests requesting weak RC4 cipher for brute-force offline cracking.',
      anomalyFeatures: [
        { label: 'Legacy Cipher Downgrade (RC4)', impact: 97 },
        { label: 'Ticket Request Velocity Anomaly', impact: 93 },
        { label: 'High Privilege SPN Target', impact: 88 },
        { label: 'Endpoint Profile Divergence', impact: 82 }
      ],
      mitreTechnique: 'T1558.003 (Steal or Forge Kerberos Tickets: Kerberoasting)',
      zeroTrustVerdict: 'SESSION QUARANTINED • HIGH ANOMALY SCORE',
      containmentAction: 'Host network port 88 isolated, alerted tier-2 SOC with decodable hash bundle.'
    }
  },
  {
    id: 'sqli',
    name: 'Adversarial SQLi & Data Exfil Probe',
    category: 'Web Exploitation',
    severity: 'HIGH',
    confidence: 98.7,
    sampleLog: `GET /api/v1/students?dept=CSE' UNION ALL SELECT null,username,password_hash,null FROM admin_credentials WHERE '1'='1 HTTP/1.1
Origin: 45.142.212.89 (Tor Exit Relay)
ResponseCode: 500 (Leakage in stacktrace) | Latency: 22ms`,
    explanation: {
      rootCause: 'Structured SQL token injection combined with high-risk anonymous proxy origin targeting administrative credentials.',
      anomalyFeatures: [
        { label: 'SQL Token Syntax Grammar Break', impact: 99 },
        { label: 'Known Anonymous Proxy IP', impact: 95 },
        { label: 'Table Schema Enumeration Signature', impact: 92 },
        { label: 'Database Stacktrace Triggered', impact: 89 }
      ],
      mitreTechnique: 'T1190 (Exploit Public-Facing Application) / T1005 (Data from Local System)',
      zeroTrustVerdict: 'INBOUND IP DROP • ZERO INTEGRITY TRUST',
      containmentAction: 'Instant IP block inserted into edge WAF within 8 milliseconds.'
    }
  },
  {
    id: 'benign',
    name: 'Sanctioned Git LFS Developer Sync',
    category: 'Authorized Workload',
    severity: 'SAFE',
    confidence: 99.8,
    sampleLog: `POST /org/bput-hackathon-2026/vex-core.git/info/lfs/objects/batch HTTP/1.1
Host: github.bput.internal
Authorization: Bearer sha256_verified_mfa_token_f92b...
ClientIP: 10.0.12.44 | Verified mTLS Client Cert: BPUT-CA-2026-ENG`,
    explanation: {
      rootCause: 'Fully authenticated, mTLS-signed cryptographic handshake matching approved developer signature baseline.',
      anomalyFeatures: [
        { label: 'Valid Hardware Security Key MFA', impact: 2 },
        { label: 'Known Subnet & Trusted Cert', impact: 1 },
        { label: 'Normal Traffic Volume Distribution', impact: 4 },
        { label: 'No Malicious AST Tokens', impact: 0 }
      ],
      mitreTechnique: 'N/A (Legitimate Engineering Traffic)',
      zeroTrustVerdict: 'TRUST VERIFIED • CONTINUOUS MONITORING ACTIVE',
      containmentAction: 'Permitted without latency throttling.'
    }
  }
];

export const InteractiveThreatLab: React.FC = () => {
  const [selectedScenario, setSelectedScenario] = useState<AttackScenario>(SCENARIOS[0]);
  const [isScanning, setIsScanning] = useState(false);
  const [activeTab, setActiveTab] = useState<'explanation' | 'rawLog' | 'rules'>('explanation');

  const handleSelectScenario = (scenario: AttackScenario) => {
    setSelectedScenario(scenario);
    sounds.playClick(700, 0.04);
  };

  const handleTriggerScan = () => {
    setIsScanning(true);
    sounds.playScan();
    setTimeout(() => {
      setIsScanning(false);
      sounds.playClick(900, 0.04);
    }, 700);
  };

  const getSeverityBadge = (sev: AttackScenario['severity']) => {
    switch (sev) {
      case 'CRITICAL':
        return <span className="px-2.5 py-1 rounded bg-red-600 text-white font-mono text-xs font-bold shadow-solid-card">CRITICAL THREAT</span>;
      case 'HIGH':
        return <span className="px-2.5 py-1 rounded bg-amber-600 text-white font-mono text-xs font-bold shadow-solid-card">HIGH RISK</span>;
      case 'MEDIUM':
        return <span className="px-2.5 py-1 rounded bg-yellow-600 text-black font-mono text-xs font-bold">MEDIUM RISK</span>;
      case 'SAFE':
        return <span className="px-2.5 py-1 rounded bg-emerald-600 text-white font-mono text-xs font-bold shadow-solid-emerald">VERIFIED CLEAN</span>;
    }
  };

  return (
    <section id="threat-lab" className="py-20 bg-[#070910] border-b-2 border-[#1E293B] relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        {/* Header */}
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-12 gap-6">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-md bg-emerald-500/10 border border-emerald-500/40 text-emerald-400 font-mono text-xs font-bold uppercase tracking-wider mb-3">
              <Terminal className="w-3.5 h-3.5" />
              <span>Interactive Simulator</span>
            </div>
            <h2 className="text-3xl sm:text-4xl font-heading font-black text-white">
              VEX Neural Threat Lab &amp; XAI Console
            </h2>
            <p className="text-slate-400 font-mono text-xs mt-1">
              Test how VEX fulfills PS09: "Trust Nothing. Explain Everything."
            </p>
          </div>

          <div className="text-xs font-mono text-slate-400 flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse" />
            <span>AI Neural Inference: <strong>Active (11.4 ms avg)</strong></span>
          </div>
        </div>

        {/* Simulator Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
          
          {/* Left Column: Attack Scenario Picker */}
          <div className="lg:col-span-4 space-y-3">
            <div className="text-xs font-mono text-slate-400 uppercase tracking-wider mb-2 font-bold">
              Select Real-World Test Vector:
            </div>

            {SCENARIOS.map((scen) => {
              const isSelected = scen.id === selectedScenario.id;
              return (
                <button
                  key={scen.id}
                  onClick={() => handleSelectScenario(scen)}
                  className={`w-full text-left p-4 rounded-xl border-2 transition-all flex flex-col justify-between ${
                    isSelected
                      ? 'bg-[#101827] border-blue-500 shadow-solid-blue -translate-y-1'
                      : 'bg-[#0A0E17] border-[#1E293B] hover:border-slate-600 hover:bg-[#0D1322]'
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] font-mono text-blue-400 font-bold uppercase">
                      {scen.category}
                    </span>
                    {getSeverityBadge(scen.severity)}
                  </div>
                  <div className="text-sm font-heading font-bold text-white mt-2">
                    {scen.name}
                  </div>
                  <div className="text-[11px] font-mono text-slate-400 mt-2 flex items-center justify-between">
                    <span>Conf: {scen.confidence}%</span>
                    <span className="flex items-center gap-1 text-blue-400">
                      Test Vector <ArrowRight className="w-3 h-3" />
                    </span>
                  </div>
                </button>
              );
            })}

            <button
              onClick={handleTriggerScan}
              disabled={isScanning}
              className="w-full mt-4 py-3 rounded-xl solid-btn-blue font-mono font-bold text-xs uppercase tracking-wider flex items-center justify-center gap-2 shadow-solid-blue cursor-pointer"
            >
              <RefreshCw className={`w-4 h-4 ${isScanning ? 'animate-spin' : ''}`} />
              <span>{isScanning ? 'Inferencing Attention Weights...' : 'Re-Run VEX XAI Pipeline'}</span>
            </button>
          </div>

          {/* Right Column: Active Terminal & XAI Breakdown */}
          <div className="lg:col-span-8 bg-[#0A0E1A] border-2 border-[#1E293B] rounded-2xl p-6 shadow-2xl flex flex-col justify-between">
            
            {/* Terminal Top Bar */}
            <div>
              <div className="flex flex-wrap items-center justify-between pb-4 border-b border-[#1E293B] gap-4">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 rounded-full bg-red-500" />
                  <div className="w-3 h-3 rounded-full bg-amber-500" />
                  <div className="w-3 h-3 rounded-full bg-emerald-500" />
                  <span className="font-mono text-xs text-slate-400 ml-2">
                    vex://forensic-core/inspect/{selectedScenario.id}
                  </span>
                </div>

                {/* Sub-tabs */}
                <div className="flex items-center gap-2 bg-[#06080E] p-1 rounded-lg border border-[#1E293B]">
                  <button
                    onClick={() => {
                      setActiveTab('explanation');
                      sounds.playClick(650, 0.03);
                    }}
                    className={`px-3 py-1 rounded text-xs font-mono font-semibold transition-colors ${
                      activeTab === 'explanation' ? 'bg-blue-600 text-white font-bold' : 'text-slate-400 hover:text-white'
                    }`}
                  >
                    XAI Breakdown
                  </button>
                  <button
                    onClick={() => {
                      setActiveTab('rawLog');
                      sounds.playClick(650, 0.03);
                    }}
                    className={`px-3 py-1 rounded text-xs font-mono font-semibold transition-colors ${
                      activeTab === 'rawLog' ? 'bg-blue-600 text-white font-bold' : 'text-slate-400 hover:text-white'
                    }`}
                  >
                    Raw Ingestion
                  </button>
                  <button
                    onClick={() => {
                      setActiveTab('rules');
                      sounds.playClick(650, 0.03);
                    }}
                    className={`px-3 py-1 rounded text-xs font-mono font-semibold transition-colors ${
                      activeTab === 'rules' ? 'bg-blue-600 text-white font-bold' : 'text-slate-400 hover:text-white'
                    }`}
                  >
                    Zero-Trust Policy
                  </button>
                </div>
              </div>

              {/* Status Banner */}
              <div className="my-5 p-4 rounded-xl bg-[#0F1626] border-2 border-slate-700/60 flex flex-wrap items-center justify-between gap-4">
                <div className="flex items-center gap-3">
                  {selectedScenario.severity === 'SAFE' ? (
                    <div className="w-10 h-10 rounded-lg bg-emerald-500/20 border-2 border-emerald-400 flex items-center justify-center text-emerald-400">
                      <ShieldCheck className="w-6 h-6" />
                    </div>
                  ) : (
                    <div className="w-10 h-10 rounded-lg bg-red-500/20 border-2 border-red-500 flex items-center justify-center text-red-400">
                      <ShieldAlert className="w-6 h-6" />
                    </div>
                  )}

                  <div>
                    <div className="text-xs font-mono text-slate-400 uppercase">Detection Verdict</div>
                    <div className="text-base font-heading font-black text-white">
                      {selectedScenario.explanation.zeroTrustVerdict}
                    </div>
                  </div>
                </div>

                <div className="text-right">
                  <div className="text-[10px] font-mono text-slate-400 uppercase">Confidence Score</div>
                  <div className="text-2xl font-mono font-black text-blue-400">
                    {selectedScenario.confidence}%
                  </div>
                </div>
              </div>

              {/* Tab 1: XAI Explanation (The Core of PS09) */}
              {activeTab === 'explanation' && (
                <div className="space-y-5">
                  <div>
                    <h4 className="text-xs font-mono uppercase text-blue-400 font-bold mb-1.5 flex items-center gap-1.5">
                      <Cpu className="w-4 h-4" />
                      1. Natural Language Root Cause Reasoning
                    </h4>
                    <p className="text-sm text-slate-200 bg-[#080C16] p-3.5 rounded-xl border border-slate-700/60 leading-relaxed font-sans">
                      {selectedScenario.explanation.rootCause}
                    </p>
                  </div>

                  {/* Feature Impact Bars */}
                  <div>
                    <h4 className="text-xs font-mono uppercase text-amber-400 font-bold mb-2.5 flex items-center gap-1.5">
                      <Layers className="w-4 h-4" />
                      2. Neural Attention Feature Attribution (Explainable AI)
                    </h4>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      {selectedScenario.explanation.anomalyFeatures.map((feat, idx) => (
                        <div key={idx} className="bg-[#080C16] p-3 rounded-lg border border-slate-800">
                          <div className="flex justify-between text-xs font-mono mb-1.5">
                            <span className="text-slate-300 font-medium">{feat.label}</span>
                            <span className="text-blue-400 font-bold">{feat.impact}%</span>
                          </div>
                          <div className="w-full h-2 bg-slate-800 rounded-full overflow-hidden">
                            <div
                              className={`h-full rounded-full transition-all duration-700 ${
                                feat.impact > 80 ? 'bg-red-500' : feat.impact > 50 ? 'bg-amber-500' : 'bg-emerald-500'
                              }`}
                              style={{ width: `${feat.impact}%` }}
                            />
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* MITRE ATT&CK */}
                  <div className="p-3 bg-[#080C16] rounded-xl border border-purple-500/30 flex items-center justify-between text-xs font-mono">
                    <span className="text-purple-400 font-bold">MITRE ATT&CK Mapping:</span>
                    <span className="text-slate-200">{selectedScenario.explanation.mitreTechnique}</span>
                  </div>
                </div>
              )}

              {/* Tab 2: Raw Log */}
              {activeTab === 'rawLog' && (
                <div className="space-y-3">
                  <div className="text-xs font-mono text-slate-400">
                    Ingested Payload Stream (Normalized Syslog/RFC 5424):
                  </div>
                  <pre className="p-4 rounded-xl bg-[#05070D] border border-slate-800 font-mono text-xs text-blue-300 overflow-x-auto leading-relaxed">
                    {selectedScenario.sampleLog}
                  </pre>
                </div>
              )}

              {/* Tab 3: Zero-Trust Containment Rule */}
              {activeTab === 'rules' && (
                <div className="space-y-4">
                  <div className="p-4 rounded-xl bg-[#080C16] border border-emerald-500/40">
                    <div className="text-xs font-mono text-emerald-400 font-bold uppercase mb-1">
                      Automated Reactive Micro-Segmentation
                    </div>
                    <p className="text-sm text-slate-200">
                      {selectedScenario.explanation.containmentAction}
                    </p>
                  </div>

                  <div className="p-3 rounded-xl bg-[#05070D] border border-slate-800 font-mono text-xs text-slate-300">
                    <p className="text-slate-500">// Dynamically Generated Kernel Policy:</p>
                    <p className="text-emerald-400 font-bold mt-1">
                      iptables -A VEX_ZERO_TRUST_DROP -m string --hex-string "|{selectedScenario.id}|" -j REJECT --reject-with icmp-port-unreachable
                    </p>
                  </div>
                </div>
              )}
            </div>

            {/* Bottom Status bar */}
            <div className="mt-6 pt-4 border-t border-[#1E293B] flex flex-wrap items-center justify-between text-xs font-mono text-slate-400">
              <span>Platform: VEX Log Engine v0.9.8</span>
              <span className="text-slate-300">Target Framework: BPUT-2026-PS09</span>
            </div>
          </div>

        </div>

      </div>
    </section>
  );
};
