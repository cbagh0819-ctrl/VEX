import React, { useState } from 'react';
import { Users, ExternalLink, Image as ImageIcon, Code2, Upload, GraduationCap, Building2, Award, BookOpen } from 'lucide-react';
import { GithubIcon, LinkedinIcon, InstagramIcon } from './Icons';
import { sounds } from '../utils/audio';
import { asset } from '../utils/assets';

interface TeamMember {
  id: string;
  name: string;
  role: string;
  specialty: string;
  badgeColor: string;
  solidBg: string;
  initials: string;
  githubUrl: string;
  linkedinUrl?: string;
  instagramUrl?: string;
  instagramHandle?: string;
  bio: string;
  photoUrl?: string;
}

const MEMBERS: TeamMember[] = [
  {
    id: 'ankit',
    name: 'Ankit Mandi',
    role: 'Team Lead & AI Architect',
    specialty: 'Transformer Threat Models, Log Forensics & XAI',
    badgeColor: 'border-blue-500 shadow-solid-blue',
    solidBg: 'bg-blue-600',
    initials: 'AM',
    githubUrl: 'https://github.com/ankitmandi',
    linkedinUrl: 'https://linkedin.com/in/ankit-mandi',
    instagramUrl: 'https://www.instagram.com/shades.of.ankit/',
    instagramHandle: '@shades.of.ankit',
    bio: 'Spearheaded the overall VEX neural architecture, lead development for PS09 compliance, and authored the explainable attention attribution pipeline.'
  },
  {
    id: 'chandan',
    name: 'Chandan Bagh',
    role: 'Backend Systems & Linux Attack Testing Lead',
    specialty: 'Linux Exploit Simulation, Backend Telemetry & High-Performance Systems',
    badgeColor: 'border-emerald-500 shadow-solid-emerald',
    solidBg: 'bg-emerald-600',
    initials: 'CB',
    githubUrl: 'https://github.com/cbagh0819-ctrl',
    linkedinUrl: 'https://linkedin.com/in/chandan-bagh',
    instagramUrl: 'https://www.instagram.com/chandan.bagh07/',
    instagramHandle: '@chandan.bagh07',
    bio: 'Creator of GODZIP | 3rd Place Winner at BPUT Hackathon 2026. Architected Linux attack testing suites, real-time FastAPI telemetry, and the offline Android data cache that secured our podium finish.'
  },
  {
    id: 'deepak',
    name: 'Deepak Dash',
    role: 'Security Researcher & Zero-Trust Specialist',
    specialty: 'Adversarial Payloads, MITRE Mapping & eBPF',
    badgeColor: 'border-amber-500 shadow-solid-gold',
    solidBg: 'bg-amber-600',
    initials: 'DD',
    githubUrl: 'https://github.com/deepakdash',
    linkedinUrl: 'https://linkedin.com/in/deepak-dash',
    instagramUrl: 'https://www.instagram.com/deep.ak._3/',
    instagramHandle: '@deep.ak._3',
    bio: 'Designed the adversarial defense test battery, MITRE ATT&CK killchain correlations, and dynamic kernel micro-segmentation rules.'
  },
  {
    id: 'omm',
    name: 'Omm Prakash Mallik',
    role: 'Explainable AI & NLP Threat Researcher',
    specialty: 'Homoglyph Detection, Lexical NLP & Shapley Weights',
    badgeColor: 'border-purple-500 shadow-solid-card',
    solidBg: 'bg-purple-600',
    initials: 'OM',
    githubUrl: 'https://github.com/ommprakashmallik',
    linkedinUrl: 'https://linkedin.com/in/omm-prakash-mallik',
    instagramUrl: 'https://www.instagram.com/krrishna_21/',
    instagramHandle: '@krrishna_21',
    bio: 'Built the anti-impersonation engine, Unicode visual similarity scoring, and the mathematical Shapley value interpreter.'
  },
  {
    id: 'padmalochan',
    name: 'Padmalochan Behera',
    role: 'Fullstack Systems & 3D Interactive UI Lead',
    specialty: 'React, Three.js Spatial Topologies & Client SDK',
    badgeColor: 'border-cyan-500 shadow-solid-blue',
    solidBg: 'bg-cyan-600',
    initials: 'PB',
    githubUrl: 'https://github.com/padmalochanbehera',
    linkedinUrl: undefined, // Pending: to be added later as requested
    instagramUrl: 'https://www.instagram.com/cheeckuu/',
    instagramHandle: '@cheeckuu',
    bio: 'Developed the reactive 3D threat visualizer, solid geometric canvas, web application console, and responsive client interfaces.'
  }
];

export const TeamSection: React.FC = () => {
  const [memberPhotos, setMemberPhotos] = useState<{ [id: string]: string }>({
    mentor_kd_naik: asset('/images/mentors/kodanda-dhar-naik.jpg'),
    mentor: asset('/images/mentors/kodanda-dhar-naik.jpg'),
    mentor_kamalakanta: asset('/images/mentors/kamalakanta-rout.jpg'),
    ankit: asset('/images/team/member_3.jpg'),
    chandan: asset('/images/team/member_4.jpg'),
    deepak: asset('/images/team/member_2.jpg'),
    omm: asset('/images/team/member_1.jpg'),
    padmalochan: asset('/images/team/member_5.jpg')
  });
  const [activeUploadId, setActiveUploadId] = useState<string | null>(null);
  const [tempUrlInput, setTempUrlInput] = useState('');

  const handlePhotoUpload = (id: string, e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const url = URL.createObjectURL(file);
      setMemberPhotos(prev => ({ ...prev, [id]: url }));
      sounds.playClick(950, 0.05);
    }
  };

  const handleUrlSubmit = (id: string) => {
    if (tempUrlInput.trim()) {
      setMemberPhotos(prev => ({ ...prev, [id]: tempUrlInput.trim() }));
      setTempUrlInput('');
      setActiveUploadId(null);
      sounds.playClick(900, 0.04);
    }
  };

  return (
    <section id="team-section" className="py-20 bg-[#06080E] border-b-2 border-[#1E293B] relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        {/* Section Header */}
        <div className="flex flex-col items-center text-center mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-md bg-amber-500/10 border border-amber-500/40 text-amber-400 font-mono text-xs font-bold uppercase tracking-wider mb-3">
            <Users className="w-3.5 h-3.5" />
            <span>The Builders Behind VEX</span>
          </div>

          <h2 className="text-3xl sm:text-5xl font-heading font-black text-white tracking-tight">
            Team <span className="text-amber-400">VEX LOG</span>
          </h2>

          <p className="mt-4 text-slate-300 max-w-2xl text-base leading-relaxed">
            Representing <a href="https://pmec.ac.in/" target="_blank" rel="noopener noreferrer" className="text-blue-400 hover:text-blue-300 underline font-semibold">Parala Maharaja Engineering College (PMEC)</a>, Berhampur. 
            The 5-member student engineering cohort and faculty mentors who achieved the <strong className="text-amber-400">3rd Prize victory</strong> at <strong className="text-white">BPUT Hackathon 2026</strong> for Problem Statement 09 (CYBERGUARD).
          </p>

          <div className="mt-4 inline-flex items-center gap-2 text-xs font-mono text-slate-400 bg-[#0F1626] px-4 py-2 rounded-xl border border-slate-800">
            <ImageIcon className="w-4 h-4 text-blue-400" />
            <span>Photo placeholders active. You can upload or link photos directly on any card!</span>
          </div>
        </div>

        {/* 🌟 FEATURED FACULTY MENTORS & GUIDANCE */}
        <div className="mb-16">
          <div className="text-xs font-mono text-amber-400 uppercase tracking-widest font-bold mb-4 flex items-center gap-2">
            <GraduationCap className="w-4 h-4 text-amber-400" />
            <span>Faculty Mentorship &amp; Department Guidance</span>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            
            {/* MENTOR 1: Mr. Kodanda Dhar Naik */}
            <div className="bg-gradient-to-r from-[#0E1528] via-[#10192F] to-[#0A101F] border-2 border-amber-500/70 rounded-2xl p-6 sm:p-7 shadow-solid-gold relative overflow-hidden group flex flex-col justify-between">
              {/* Top Right Honor Badge */}
              <div className="absolute top-0 right-0 bg-gradient-to-l from-amber-500 to-yellow-500 text-black font-mono text-[10px] font-black uppercase px-3.5 py-1 rounded-bl-xl tracking-wider flex items-center gap-1 shadow-sm">
                <Award className="w-3.5 h-3.5 fill-black" />
                <span>Head Mentor</span>
              </div>

              <div>
                <div className="flex items-start gap-4 mb-4">
                  {/* Photo Slot */}
                  <div className="relative w-20 h-20 sm:w-24 sm:h-24 rounded-2xl bg-[#17233D] border-2 border-amber-400/80 overflow-hidden flex items-center justify-center shrink-0 shadow-inner group/photo">
                    {memberPhotos['mentor_kd_naik'] || memberPhotos['mentor'] ? (
                      <img
                        src={memberPhotos['mentor_kd_naik'] || memberPhotos['mentor']}
                        alt="Mr. Kodanda Dhar Naik (K.D. Naik)"
                        className="w-full h-full object-cover object-top"
                      />
                    ) : (
                      <div className="w-full h-full bg-gradient-to-br from-amber-600 to-yellow-600 flex flex-col items-center justify-center text-white">
                        <span className="font-heading font-black text-2xl tracking-wider text-black">
                          KDN
                        </span>
                        <span className="text-[8px] font-mono text-black font-extrabold uppercase mt-0.5">
                          FACULTY • CSE
                        </span>
                      </div>
                    )}

                    <label
                      className="absolute inset-0 bg-black/75 opacity-0 group-hover/photo:opacity-100 flex flex-col items-center justify-center text-white text-[10px] font-mono cursor-pointer transition-opacity"
                      title="Upload Mentor Photo"
                    >
                      <Upload className="w-4 h-4 mb-1 text-amber-400" />
                      <span>Add Photo</span>
                      <input
                        type="file"
                        accept="image/*"
                        className="hidden"
                        onChange={(e) => handlePhotoUpload('mentor_kd_naik', e)}
                      />
                    </label>
                  </div>

                  <div>
                    <div className="flex flex-wrap items-center gap-1.5 mb-1">
                      <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-amber-500/20 text-amber-300 border border-amber-500/40 font-bold uppercase">
                        Lead Faculty Mentor
                      </span>
                    </div>

                    <h3 className="text-xl sm:text-2xl font-heading font-black text-white">
                      Mr. Kodanda Dhar Naik <span className="text-amber-400 font-semibold text-lg sm:text-xl">(K.D. Naik)</span>
                    </h3>

                    <div className="text-xs sm:text-sm text-slate-200 font-semibold mt-0.5">
                      Assistant Professor
                    </div>

                    <div className="text-xs font-mono text-blue-400 mt-1">
                      Department of Computer Science &amp; Engineering
                    </div>
                  </div>
                </div>

                <div className="text-xs font-mono text-slate-400 flex items-center gap-1.5 mb-3 bg-[#080D1A] p-2 rounded-lg border border-slate-800">
                  <Building2 className="w-3.5 h-3.5 text-amber-400 shrink-0" />
                  <a
                    href="https://pmec.ac.in/"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-amber-300 hover:text-amber-200 font-semibold underline truncate"
                  >
                    Parala Maharaja Engineering College (PMEC), Berhampur
                  </a>
                </div>

                <p className="text-xs sm:text-sm text-slate-300 leading-relaxed">
                  Provided foundational technical direction, domain oversight in cybersecurity and explainable AI paradigms, 
                  and department stewardship that guided <strong className="text-white">Team VEX LOG</strong> to secure the 
                  prestigious <strong className="text-amber-400">3rd Prize podium finish</strong> at BPUT Hackathon 2026.
                </p>
              </div>

              {/* Bottom Card Action */}
              <div className="mt-5 pt-3 border-t border-slate-800/80 flex items-center justify-between">
                <a
                  href="https://pmec.ac.in/"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-1 text-[11px] font-mono text-amber-300 hover:text-white"
                >
                  <span>PMEC Portal</span>
                  <ExternalLink className="w-3 h-3" />
                </a>

                <button
                  onClick={() => {
                    setActiveUploadId(activeUploadId === 'mentor_kd_naik' ? null : 'mentor_kd_naik');
                    sounds.playClick(650, 0.03);
                  }}
                  className="px-2.5 py-1 rounded text-[11px] font-mono text-slate-300 hover:text-amber-300 bg-slate-800/60 border border-slate-700 flex items-center gap-1 cursor-pointer transition-colors"
                >
                  <ImageIcon className="w-3.5 h-3.5 text-amber-400" />
                  <span>{memberPhotos['mentor_kd_naik'] || memberPhotos['mentor'] ? 'Change Photo' : 'Attach Photo'}</span>
                </button>
              </div>

              {/* Dropdown Input for Image URL */}
              {activeUploadId === 'mentor_kd_naik' && (
                <div className="mt-3 p-3 bg-[#080C16] rounded-xl border border-amber-500/40">
                  <div className="text-[10px] font-mono text-amber-400 mb-1">
                    Paste Photo URL for Mr. K.D. Naik:
                  </div>
                  <div className="flex gap-2">
                    <input
                      type="url"
                      placeholder="https://.../kd-naik.jpg"
                      value={tempUrlInput}
                      onChange={(e) => setTempUrlInput(e.target.value)}
                      className="w-full bg-[#0F1626] border border-slate-700 text-xs text-white px-2.5 py-1.5 rounded-lg font-mono focus:outline-none focus:border-amber-500"
                    />
                    <button
                      onClick={() => handleUrlSubmit('mentor_kd_naik')}
                      className="px-3 py-1 bg-amber-500 text-black rounded-lg text-xs font-mono font-bold hover:bg-amber-400"
                    >
                      Set
                    </button>
                  </div>
                </div>
              )}
            </div>

            {/* MENTOR 2: Mr. Kamalakanta Rout */}
            <div className="bg-gradient-to-r from-[#0D1525] via-[#0F182C] to-[#0A101C] border-2 border-blue-500/60 rounded-2xl p-6 sm:p-7 shadow-solid-blue relative overflow-hidden group flex flex-col justify-between">
              {/* Top Right Badge */}
              <div className="absolute top-0 right-0 bg-blue-600 text-white font-mono text-[10px] font-black uppercase px-3.5 py-1 rounded-bl-xl tracking-wider flex items-center gap-1 shadow-sm">
                <BookOpen className="w-3.5 h-3.5" />
                <span>Faculty Mentor</span>
              </div>

              <div>
                <div className="flex items-start gap-4 mb-4">
                  {/* Photo Slot */}
                  <div className="relative w-20 h-20 sm:w-24 sm:h-24 rounded-2xl bg-[#131F36] border-2 border-blue-400/80 overflow-hidden flex items-center justify-center shrink-0 shadow-inner group/photo">
                    {memberPhotos['mentor_kamalakanta'] ? (
                      <img
                        src={memberPhotos['mentor_kamalakanta']}
                        alt="Mr. Kamalakanta Rout"
                        className="w-full h-full object-cover object-top"
                      />
                    ) : (
                      <div className="w-full h-full bg-gradient-to-br from-blue-600 to-indigo-700 flex flex-col items-center justify-center text-white">
                        <span className="font-heading font-black text-2xl tracking-wider text-white">
                          KR
                        </span>
                        <span className="text-[8px] font-mono text-blue-200 font-extrabold uppercase mt-0.5">
                          CSE • PMEC
                        </span>
                      </div>
                    )}

                    <label
                      className="absolute inset-0 bg-black/75 opacity-0 group-hover/photo:opacity-100 flex flex-col items-center justify-center text-white text-[10px] font-mono cursor-pointer transition-opacity"
                      title="Upload Mentor Photo"
                    >
                      <Upload className="w-4 h-4 mb-1 text-blue-400" />
                      <span>Add Photo</span>
                      <input
                        type="file"
                        accept="image/*"
                        className="hidden"
                        onChange={(e) => handlePhotoUpload('mentor_kamalakanta', e)}
                      />
                    </label>
                  </div>

                  <div>
                    <div className="flex flex-wrap items-center gap-1.5 mb-1">
                      <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-blue-500/20 text-blue-300 border border-blue-500/40 font-bold uppercase">
                        Faculty Advisor
                      </span>
                    </div>

                    <h3 className="text-xl sm:text-2xl font-heading font-black text-white">
                      Mr. Kamalakanta Rout
                    </h3>

                    <div className="text-xs sm:text-sm text-slate-200 font-semibold mt-0.5">
                      Faculty Member in Computer Science &amp; Engineering
                    </div>

                    <div className="text-xs font-mono text-blue-400 mt-1">
                      Department of Computer Science &amp; Engineering
                    </div>
                  </div>
                </div>

                <div className="text-xs font-mono text-slate-400 flex items-center gap-1.5 mb-3 bg-[#080D1A] p-2 rounded-lg border border-slate-800">
                  <Building2 className="w-3.5 h-3.5 text-blue-400 shrink-0" />
                  <a
                    href="https://pmec.ac.in/"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-blue-300 hover:text-blue-200 font-semibold underline truncate"
                  >
                    Parala Maharaja Engineering College (PMEC), Berhampur
                  </a>
                </div>

                <p className="text-xs sm:text-sm text-slate-300 leading-relaxed">
                  Extended valuable technical assistance, advisory feedback on algorithmic implementation, and 
                  active department encouragement to support <strong className="text-white">Team VEX LOG</strong> during their 
                  solution development for BPUT Hackathon 2026.
                </p>
              </div>

              {/* Bottom Card Action */}
              <div className="mt-5 pt-3 border-t border-slate-800/80 flex items-center justify-between">
                <a
                  href="https://pmec.ac.in/"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-1 text-[11px] font-mono text-blue-300 hover:text-white"
                >
                  <span>PMEC Portal</span>
                  <ExternalLink className="w-3 h-3" />
                </a>

                <button
                  onClick={() => {
                    setActiveUploadId(activeUploadId === 'mentor_kamalakanta' ? null : 'mentor_kamalakanta');
                    sounds.playClick(650, 0.03);
                  }}
                  className="px-2.5 py-1 rounded text-[11px] font-mono text-slate-300 hover:text-blue-300 bg-slate-800/60 border border-slate-700 flex items-center gap-1 cursor-pointer transition-colors"
                >
                  <ImageIcon className="w-3.5 h-3.5 text-blue-400" />
                  <span>{memberPhotos['mentor_kamalakanta'] ? 'Change Photo' : 'Attach Photo'}</span>
                </button>
              </div>

              {/* Dropdown Input for Image URL */}
              {activeUploadId === 'mentor_kamalakanta' && (
                <div className="mt-3 p-3 bg-[#080C16] rounded-xl border border-blue-500/40">
                  <div className="text-[10px] font-mono text-blue-400 mb-1">
                    Paste Photo URL for Mr. Kamalakanta Rout:
                  </div>
                  <div className="flex gap-2">
                    <input
                      type="url"
                      placeholder="https://.../kamalakanta-rout.jpg"
                      value={tempUrlInput}
                      onChange={(e) => setTempUrlInput(e.target.value)}
                      className="w-full bg-[#0F1626] border border-slate-700 text-xs text-white px-2.5 py-1.5 rounded-lg font-mono focus:outline-none focus:border-blue-500"
                    />
                    <button
                      onClick={() => handleUrlSubmit('mentor_kamalakanta')}
                      className="px-3 py-1 bg-blue-600 text-white rounded-lg text-xs font-mono font-bold hover:bg-blue-500"
                    >
                      Set
                    </button>
                  </div>
                </div>
              )}
            </div>

          </div>
        </div>

        {/* 📸 TEAM VEX LOG OFFICIAL PHOTO HIGHLIGHTS */}
        <div className="mb-14 grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-[#0B101D] border-2 border-emerald-500/40 rounded-2xl overflow-hidden shadow-solid-emerald group">
            <div className="relative aspect-[16/10] bg-black overflow-hidden">
              <img 
                src={asset('/images/uploaded/team_certificates_all5.jpg')} 
                alt="All 5 Team VEX LOG members holding official BPUT Hackathon certificates"
                className="w-full h-full object-cover group-hover:scale-103 transition-transform duration-300"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-transparent to-transparent" />
              <div className="absolute bottom-3 left-4 right-4">
                <span className="px-2.5 py-0.5 rounded bg-emerald-600 text-white font-mono text-[10px] font-bold uppercase tracking-wider">
                  Official Award Verification
                </span>
                <p className="text-xs sm:text-sm font-semibold text-white mt-1">
                  Team VEX LOG holding our BPUT Hackathon 2026 Merit Certificates
                </p>
                <p className="text-[11px] font-mono text-slate-300">
                  Left to right: Omm Prakash Mallik, Deepak Dash, Ankit Mandi, Chandan Bagh, Padmalochan Behera
                </p>
              </div>
            </div>
          </div>

          <div className="bg-[#0B101D] border-2 border-blue-500/40 rounded-2xl overflow-hidden shadow-solid-blue group">
            <div className="relative aspect-[16/10] bg-black overflow-hidden">
              <img 
                src={asset('/images/uploaded/team_workstation_desk.jpg')} 
                alt="Team VEX LOG working together at our hackathon workstation desk"
                className="w-full h-full object-cover group-hover:scale-103 transition-transform duration-300"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-transparent to-transparent" />
              <div className="absolute bottom-3 left-4 right-4">
                <span className="px-2.5 py-0.5 rounded bg-blue-600 text-white font-mono text-[10px] font-bold uppercase tracking-wider">
                  The Hackathon Station Desk
                </span>
                <p className="text-xs sm:text-sm font-semibold text-white mt-1">
                  All 5 members in the arena with official "VEX LOG - PROBLEM 09" standee
                </p>
                <p className="text-[11px] font-mono text-slate-300">
                  Problem Statement 09 arena at BPUT Main Campus, Rourkela
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* 💻 STUDENT ENGINEERING COHORT TITLE */}
        <div className="text-xs font-mono text-blue-400 uppercase tracking-widest font-bold mb-4 flex items-center gap-2">
          <Code2 className="w-4 h-4 text-blue-400" />
          <span>Core Student Engineering Team (5 Members)</span>
        </div>

        {/* Team Members Grid (5 Cards) */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {MEMBERS.map((member) => {
            const currentPhoto = memberPhotos[member.id];
            return (
              <div
                key={member.id}
                className={`bg-[#0C101B] border-2 ${member.badgeColor} rounded-2xl p-6 transition-all duration-200 hover:-translate-y-2 flex flex-col justify-between group relative overflow-hidden`}
              >
                {/* Winner corner ribbon */}
                <div className="absolute top-0 right-0 bg-amber-500 text-black font-mono text-[9px] font-black uppercase px-3 py-0.5 rounded-bl-lg tracking-wider">
                  3rd Prize Winner
                </div>

                <div>
                  {/* Avatar / Photo Slot */}
                  <div className="flex items-center gap-4 mb-5">
                    <div className="relative w-20 h-20 rounded-2xl bg-[#141C30] border-2 border-slate-700 overflow-hidden flex items-center justify-center shrink-0 shadow-inner group/photo">
                      {currentPhoto ? (
                        <img
                          src={currentPhoto}
                          alt={member.name}
                          className="w-full h-full object-cover"
                        />
                      ) : (
                        <div className={`w-full h-full ${member.solidBg} flex flex-col items-center justify-center text-white`}>
                          <span className="font-heading font-black text-xl tracking-wider">
                            {member.initials}
                          </span>
                          <span className="text-[9px] font-mono opacity-80 uppercase">
                            VEX LOG
                          </span>
                        </div>
                      )}

                      {/* Photo Upload Overlay Button */}
                      <label
                        className="absolute inset-0 bg-black/70 opacity-0 group-hover/photo:opacity-100 flex flex-col items-center justify-center text-white text-[10px] font-mono cursor-pointer transition-opacity"
                        title="Upload Member Photo"
                      >
                        <Upload className="w-4 h-4 mb-1 text-blue-400" />
                        <span>Add Photo</span>
                        <input
                          type="file"
                          accept="image/*"
                          className="hidden"
                          onChange={(e) => handlePhotoUpload(member.id, e)}
                        />
                      </label>
                    </div>

                    <div>
                      <h3 className="text-xl font-heading font-black text-white group-hover:text-amber-400 transition-colors">
                        {member.name}
                      </h3>
                      <div className="text-xs font-mono text-blue-400 font-bold mt-0.5">
                        {member.role}
                      </div>
                      <div className="text-[11px] font-mono text-slate-400 mt-1">
                        {member.specialty}
                      </div>
                    </div>
                  </div>

                  <p className="text-slate-300 text-xs leading-relaxed font-sans mb-4">
                    {member.bio}
                  </p>
                </div>

                {/* Card Footer: Links & Photo URL Trigger */}
                <div className="pt-4 border-t border-slate-800/80 flex items-center justify-between">
                  <div className="flex items-center gap-1.5">
                    {/* GitHub */}
                    <a
                      href={member.githubUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      onClick={() => sounds.playClick(750, 0.03)}
                      className="p-2 rounded-lg bg-[#141C30] text-slate-300 hover:text-white hover:bg-slate-700 transition-colors"
                      title={`${member.name}'s GitHub`}
                    >
                      <GithubIcon className="w-4 h-4" />
                    </a>

                    {/* LinkedIn */}
                    {member.linkedinUrl ? (
                      <a
                        href={member.linkedinUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        onClick={() => sounds.playClick(750, 0.03)}
                        className="p-2 rounded-lg bg-[#141C30] text-slate-300 hover:text-white hover:bg-blue-600 transition-colors"
                        title={`${member.name}'s LinkedIn`}
                      >
                        <LinkedinIcon className="w-4 h-4" />
                      </a>
                    ) : (
                      <span
                        className="p-2 rounded-lg bg-[#0E1322] text-slate-600 border border-dashed border-slate-800 cursor-help"
                        title="LinkedIn (Adding Soon)"
                      >
                        <LinkedinIcon className="w-4 h-4 opacity-40" />
                      </span>
                    )}

                    {/* Instagram */}
                    {member.instagramUrl && (
                      <a
                        href={member.instagramUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        onClick={() => sounds.playClick(800, 0.03)}
                        className="p-2 rounded-lg bg-[#141C30] text-slate-300 hover:text-pink-400 hover:bg-pink-950/40 border border-transparent hover:border-pink-500/40 transition-colors"
                        title={`${member.name}'s Instagram (${member.instagramHandle})`}
                      >
                        <InstagramIcon className="w-4 h-4" />
                      </a>
                    )}
                  </div>

                  {/* Photo URL Setter trigger */}
                  <button
                    onClick={() => {
                      setActiveUploadId(activeUploadId === member.id ? null : member.id);
                      sounds.playClick(650, 0.03);
                    }}
                    className="text-[11px] font-mono text-slate-400 hover:text-amber-400 flex items-center gap-1 cursor-pointer"
                  >
                    <ImageIcon className="w-3.5 h-3.5" />
                    <span>{currentPhoto ? 'Change Photo' : 'Attach Photo'}</span>
                  </button>
                </div>

                {/* Dropdown Input for Image URL */}
                {activeUploadId === member.id && (
                  <div className="mt-3 p-3 bg-[#080C16] border border-blue-500/40 rounded-xl">
                    <div className="text-[10px] font-mono text-slate-400 mb-1">
                      Paste Photo URL for {member.name}:
                    </div>
                    <div className="flex gap-2">
                      <input
                        type="url"
                        placeholder="https://.../photo.jpg"
                        value={tempUrlInput}
                        onChange={(e) => setTempUrlInput(e.target.value)}
                        className="w-full bg-[#0F1626] border border-slate-700 text-xs text-white px-2.5 py-1.5 rounded-lg font-mono focus:outline-none focus:border-blue-500"
                      />
                      <button
                        onClick={() => handleUrlSubmit(member.id)}
                        className="px-3 py-1 bg-blue-600 text-white rounded-lg text-xs font-mono font-bold hover:bg-blue-500"
                      >
                        Set
                      </button>
                    </div>
                  </div>
                )}
              </div>
            );
          })}
        </div>

        {/* Official GitHub Repository Showcase Box */}
        <div className="mt-16 bg-[#0B0F1A] border-2 border-blue-500/50 rounded-2xl p-6 sm:p-8 shadow-solid-blue relative overflow-hidden">
          <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
            
            <div className="space-y-3">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded bg-blue-600/20 text-blue-400 font-mono text-xs font-bold uppercase">
                <Code2 className="w-4 h-4" />
                <span>Open Source Repository</span>
              </div>
              
              <h3 className="text-2xl sm:text-3xl font-heading font-black text-white">
                VEX-LOG / vex-threat-intelligence
              </h3>
              
              <p className="text-sm text-slate-300 max-w-2xl leading-relaxed">
                Explore the official source code submitted for BPUT Hackathon 2026 Problem Statement 09 by students of PMEC Berhampur. 
                Includes the Rust ingestion daemon, PyTorch XAI attention engine, and zero-trust policy engine.
              </p>

              {/* Terminal git clone snippet */}
              <div className="flex items-center gap-3 bg-[#06080E] p-3 rounded-xl border border-slate-800 max-w-xl font-mono text-xs text-slate-300 overflow-x-auto">
                <span className="text-emerald-400 select-none">$</span>
                <span className="text-blue-300">git clone https://github.com/cbagh0819-ctrl/VEX.git</span>
              </div>
            </div>

            {/* Direct GitHub CTA */}
            <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-4">
              <a
                href="https://github.com/cbagh0819-ctrl/VEX"
                target="_blank"
                rel="noopener noreferrer"
                onClick={() => sounds.playClick(900, 0.04)}
                className="px-6 py-3.5 rounded-xl solid-btn-blue font-mono font-bold text-xs uppercase tracking-wider flex items-center justify-center gap-2 shadow-solid-blue cursor-pointer"
              >
                <GithubIcon className="w-5 h-5 text-white" />
                <span>Star on GitHub</span>
                <ExternalLink className="w-4 h-4" />
              </a>
            </div>

          </div>
        </div>

      </div>
    </section>
  );
};
