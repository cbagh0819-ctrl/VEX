import React, { useState } from 'react';
import { X, Clock, CheckCircle2, ExternalLink } from 'lucide-react';
import { GithubIcon } from './Icons';
import { sounds } from '../utils/audio';

interface DownloadModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const DownloadModal: React.FC<DownloadModalProps> = ({ isOpen, onClose }) => {
  const [email, setEmail] = useState('');
  const [submitted, setSubmitted] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (email.trim()) {
      setSubmitted(true);
      sounds.playClick(900, 0.05);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-md animate-in fade-in duration-200">
      
      <div className="relative w-full max-w-lg bg-[#0D1220] border-2 border-blue-500/80 rounded-2xl p-6 sm:p-8 shadow-solid-blue overflow-hidden">
        
        {/* Top Accent Strip */}
        <div className="absolute top-0 left-0 right-0 h-1.5 bg-gradient-to-r from-blue-600 via-amber-500 to-emerald-500" />

        {/* Close Button */}
        <button
          onClick={() => {
            sounds.playClick(600, 0.03);
            onClose();
          }}
          className="absolute top-4 right-4 p-2 rounded-lg bg-[#141C30] text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
        >
          <X className="w-4 h-4" />
        </button>

        {/* Modal Header */}
        <div className="flex items-center gap-3 mb-4">
          <div className="w-12 h-12 rounded-xl bg-amber-500/20 border-2 border-amber-500 flex items-center justify-center text-amber-400 shadow-sm">
            <Clock className="w-6 h-6" />
          </div>
          <div>
            <div className="text-xs font-mono text-amber-400 font-bold uppercase tracking-wider">
              Release Status: Post-Hackathon Staging
            </div>
            <h3 className="text-xl font-heading font-black text-white">
              VEX v1.0 Download Coming Soon
            </h3>
          </div>
        </div>

        {/* Description */}
        <div className="space-y-3 text-sm text-slate-300 leading-relaxed font-sans">
          <p>
            Following our <strong className="text-amber-400">3rd Prize victory at BPUT Hackathon 2026</strong>, 
            the VEX engineering team is currently hardening the production runtime, finalizing automated kernel drivers, 
            and preparing official pre-built binaries for Linux (Debian/RHEL), Windows x64, and macOS.
          </p>
          <p className="text-xs text-slate-400 bg-[#080C16] p-3 rounded-xl border border-slate-800">
            <strong>Release Timeline:</strong> Direct installer downloads will be unlocked publicly following partner 
            review with Odisha Knowledge Corporation Limited (OKCL).
          </p>
        </div>

        {/* Signup Form */}
        <div className="mt-6 pt-5 border-t border-slate-800">
          {submitted ? (
            <div className="p-4 rounded-xl bg-emerald-500/10 border-2 border-emerald-500/50 flex items-center gap-3 text-emerald-400 font-mono text-xs">
              <CheckCircle2 className="w-5 h-5 shrink-0" />
              <span>You're on the early access priority list! We'll alert you the instant v1.0 binary releases.</span>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-3">
              <label className="block text-xs font-mono text-slate-300 font-bold uppercase">
                Get Notified on Binary Release:
              </label>
              <div className="flex gap-2">
                <input
                  type="email"
                  required
                  placeholder="name@university.ac.in or work email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full bg-[#080C16] border-2 border-slate-700 text-sm text-white px-3.5 py-2.5 rounded-xl font-sans focus:outline-none focus:border-blue-500"
                />
                <button
                  type="submit"
                  className="px-5 py-2.5 rounded-xl solid-btn-blue font-mono font-bold text-xs uppercase tracking-wider shrink-0"
                >
                  Notify Me
                </button>
              </div>
            </form>
          )}
        </div>

        {/* GitHub Alternative */}
        <div className="mt-6 flex items-center justify-between pt-4 border-t border-slate-800 text-xs font-mono">
          <span className="text-slate-400">Want to audit the source code?</span>
          <a
            href="https://github.com/cbagh0819-ctrl/VEX"
            target="_blank"
            rel="noopener noreferrer"
            onClick={() => sounds.playClick(800, 0.04)}
            className="flex items-center gap-1.5 text-blue-400 hover:text-blue-300 font-bold"
          >
            <GithubIcon className="w-4 h-4" />
            <span>GitHub Repository</span>
            <ExternalLink className="w-3 h-3" />
          </a>
        </div>

      </div>
    </div>
  );
};
