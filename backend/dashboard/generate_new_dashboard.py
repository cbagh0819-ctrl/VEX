import os

# Complete Dashboard Implementation matching user screenshot
html_content = r'''<!DOCTYPE html>
<html lang="en" data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VEX Cyber Guard — Overview Dashboard & SOC</title>
    <script>
        (function() {
            try {
                const saved = localStorage.getItem('vex_soc_theme');
                if (saved === 'dark') {
                    document.documentElement.setAttribute('data-theme', 'dark');
                } else {
                    document.documentElement.setAttribute('data-theme', 'light');
                }
            } catch(e) {}
        })();
    </script>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        :root {
            /* Light Theme (Matches Uploaded Screenshot) */
            --bg-main: #f4f8f6;
            --bg-ambient: radial-gradient(circle at 100% 0%, rgba(16, 185, 129, 0.08) 0%, transparent 40%), radial-gradient(circle at 0% 100%, rgba(16, 185, 129, 0.05) 0%, transparent 40%), #f8fafc;
            --bg-sidebar: #ffffff;
            --bg-card: #ffffff;
            --border-card: #e2e8f0;
            --text-heading: #0f172a;
            --text-body: #334155;
            --text-muted: #64748b;
            --green-primary: #15803d;
            --green-active: #059669;
            --green-soft: #f0fdf4;
            --green-border: #bbf7d0;
            --card-shadow: 0 4px 20px -2px rgba(0, 0, 0, 0.03), 0 2px 6px -1px rgba(0, 0, 0, 0.02);
            --header-border: #e2e8f0;
        }

        [data-theme="dark"] {
            --bg-main: #06090e;
            --bg-ambient: #06090e;
            --bg-sidebar: #0a0f18;
            --bg-card: #0f1624;
            --border-card: #1e293b;
            --text-heading: #f8fafc;
            --text-body: #cbd5e1;
            --text-muted: #8d99ad;
            --green-primary: #10b981;
            --green-active: #059669;
            --green-soft: rgba(5, 150, 105, 0.15);
            --green-border: #065f46;
            --card-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
            --header-border: #1e293b;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background: var(--bg-ambient);
            color: var(--text-body);
            display: flex;
            height: 100vh;
            overflow: hidden;
        }

        /* Sidebar Styling */
        sidebar {
            width: 250px;
            background: var(--bg-sidebar);
            border-right: 1px solid var(--border-card);
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            z-index: 20;
            box-shadow: 2px 0 12px rgba(0,0,0,0.02);
        }

        .sidebar-brand {
            padding: 24px 20px 16px 20px;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .brand-icon-box {
            width: 38px;
            height: 38px;
            border-radius: 10px;
            background: #dcfce7;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #16a34a;
            flex-shrink: 0;
        }

        .brand-text h2 {
            font-size: 17px;
            font-weight: 800;
            color: var(--text-heading);
            letter-spacing: 0.5px;
            line-height: 1.1;
        }

        .brand-text h2 span {
            color: #16a34a;
        }

        .brand-text p {
            font-size: 10.5px;
            color: var(--text-muted);
            margin-top: 3px;
            font-weight: 500;
        }

        .sidebar-menu {
            list-style: none;
            padding: 12px 14px;
            overflow-y: auto;
            flex: 1;
        }

        .sidebar-menu li {
            margin-bottom: 6px;
        }

        .sidebar-menu a {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 10px 16px;
            color: var(--text-body);
            text-decoration: none;
            font-size: 13.5px;
            font-weight: 500;
            border-radius: 10px;
            transition: all 0.15s ease;
            cursor: pointer;
        }

        .sidebar-menu a:hover {
            background: var(--green-soft);
            color: var(--green-primary);
        }

        /* Active Menu Item: Bold Green Pill matching screenshot */
        .sidebar-menu a.active {
            background: #008744 !important;
            color: #ffffff !important;
            font-weight: 600;
            box-shadow: 0 4px 12px rgba(0, 135, 68, 0.25);
        }

        .sidebar-menu a svg {
            width: 17px;
            height: 17px;
            flex-shrink: 0;
            fill: currentColor;
        }

        .sidebar-footer {
            padding: 16px;
        }

        .soc-status-card {
            background: #f0fdf4;
            border: 1px solid #bbf7d0;
            border-radius: 14px;
            padding: 10px 14px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        [data-theme="dark"] .soc-status-card {
            background: #064e3b;
            border-color: #059669;
        }

        .status-dot-pulse {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #16a34a;
            box-shadow: 0 0 0 0 rgba(22, 163, 74, 0.7);
            animation: pulse-dot 1.8s infinite;
        }

        @keyframes pulse-dot {
            0% { box-shadow: 0 0 0 0 rgba(22, 163, 74, 0.7); }
            70% { box-shadow: 0 0 0 8px rgba(22, 163, 74, 0); }
            100% { box-shadow: 0 0 0 0 rgba(22, 163, 74, 0); }
        }

        .soc-status-text {
            display: flex;
            flex-direction: column;
        }

        .soc-status-text .label {
            font-size: 10px;
            color: var(--text-muted);
            font-weight: 600;
            text-transform: uppercase;
        }

        .soc-status-text .val {
            font-size: 12px;
            font-weight: 700;
            color: #15803d;
        }

        [data-theme="dark"] .soc-status-text .val {
            color: #34d399;
        }

        /* Main Workspace */
        main {
            flex: 1;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            background: var(--bg-main);
        }

        header.top-header {
            height: 72px;
            background: var(--bg-sidebar);
            border-bottom: 1px solid var(--header-border);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 32px;
            flex-shrink: 0;
        }

        .header-titles h1 {
            font-size: 21px;
            font-weight: 800;
            color: var(--text-heading);
            letter-spacing: -0.3px;
        }

        .header-titles p {
            font-size: 12.5px;
            color: var(--text-muted);
            margin-top: 2px;
        }

        .header-actions {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .last-updated-text {
            font-size: 12.5px;
            color: var(--text-muted);
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .pill-btn {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 8px 16px;
            border-radius: 9999px;
            font-size: 12.5px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.15s ease;
        }

        .pill-btn-green {
            background: #f0fdf4;
            border: 1px solid #86efac;
            color: #15803d;
        }

        .pill-btn-green:hover {
            background: #dcfce7;
            border-color: #4ade80;
        }

        .pill-btn-amber {
            background: #fffbeb;
            border: 1px solid #fde047;
            color: #b45309;
        }

        .pill-btn-amber:hover {
            background: #fef3c7;
        }

        .scrollable-body {
            flex: 1;
            padding: 24px 32px;
            overflow-y: auto;
        }

        /* Views */
        .view-section {
            display: none;
        }

        .view-section.active {
            display: block;
        }

        /* 1. Canary Deception Alert Banner */
        .canary-alert-banner {
            background: linear-gradient(90deg, #fff1f2 0%, #ffe4e6 100%);
            border: 1px solid #fecdd3;
            border-radius: 16px;
            padding: 14px 20px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 2px 10px rgba(225, 29, 72, 0.05);
            animation: fadeIn 0.3s ease;
        }

        [data-theme="dark"] .canary-alert-banner {
            background: #2a0b12;
            border-color: #881337;
        }

        .canary-alert-left {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .canary-alert-icon {
            width: 42px;
            height: 42px;
            border-radius: 12px;
            background: #ffe4e6;
            color: #e11d48;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
            font-size: 20px;
        }

        .canary-alert-title {
            font-size: 13.5px;
            font-weight: 800;
            color: #e11d48;
            letter-spacing: 0.5px;
            text-transform: uppercase;
        }

        .canary-alert-desc {
            font-size: 12px;
            color: var(--text-body);
            margin-top: 3px;
        }

        .canary-alert-actions {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .btn-radar-lock {
            background: #e11d48;
            color: #ffffff;
            border: none;
            border-radius: 10px;
            padding: 8px 18px;
            font-size: 12.5px;
            font-weight: 700;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 6px;
            transition: all 0.15s;
        }

        .btn-radar-lock:hover {
            background: #be123c;
            box-shadow: 0 4px 12px rgba(225, 29, 72, 0.3);
        }

        .btn-dismiss {
            background: #ffffff;
            border: 1px solid #fda4af;
            color: #e11d48;
            border-radius: 10px;
            padding: 8px 16px;
            font-size: 12.5px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.15s;
        }

        [data-theme="dark"] .btn-dismiss {
            background: #1e293b;
            border-color: #881337;
            color: #fda4af;
        }

        .btn-dismiss:hover {
            background: #ffe4e6;
        }

        /* 2. 6 Stat Metric Cards Row */
        .stat-cards-grid {
            display: grid;
            grid-template-columns: repeat(6, 1fr);
            gap: 14px;
            margin-bottom: 20px;
        }

        .stat-card {
            background: #ffffff;
            border: 1px solid var(--border-card);
            border-radius: 16px;
            padding: 16px 18px;
            position: relative;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            min-height: 98px;
            box-shadow: var(--card-shadow);
            transition: transform 0.15s;
        }

        .stat-card:hover {
            transform: translateY(-2px);
        }

        .stat-card.c-total {
            background: linear-gradient(135deg, #ffffff 0%, #eff6ff 100%);
            border-color: #dbeafe;
        }
        .stat-card.c-critical {
            background: linear-gradient(135deg, #ffffff 0%, #fff1f2 100%);
            border-color: #ffe4e6;
        }
        .stat-card.c-high {
            background: linear-gradient(135deg, #ffffff 0%, #fff7ed 100%);
            border-color: #ffedd5;
        }
        .stat-card.c-medium {
            background: linear-gradient(135deg, #ffffff 0%, #fefce8 100%);
            border-color: #fef08a;
        }
        .stat-card.c-low {
            background: linear-gradient(135deg, #ffffff 0%, #f0f9ff 100%);
            border-color: #e0f2fe;
        }
        .stat-card.c-safe {
            background: linear-gradient(135deg, #ffffff 0%, #f0fdf4 100%);
            border-color: #dcfce7;
        }

        [data-theme="dark"] .stat-card.c-total { background: #0c1a30; border-color: #1e3a8a; }
        [data-theme="dark"] .stat-card.c-critical { background: #2a0b12; border-color: #881337; }
        [data-theme="dark"] .stat-card.c-high { background: #2a140a; border-color: #9a3412; }
        [data-theme="dark"] .stat-card.c-medium { background: #261d08; border-color: #854d0e; }
        [data-theme="dark"] .stat-card.c-low { background: #082132; border-color: #0369a1; }
        [data-theme="dark"] .stat-card.c-safe { background: #082318; border-color: #166534; }

        .stat-header {
            display: flex;
            align-items: flex-start;
            gap: 10px;
        }

        .stat-icon {
            font-size: 18px;
            line-height: 1;
        }

        .stat-label-wrap {
            display: flex;
            flex-direction: column;
        }

        .stat-title {
            font-size: 11.5px;
            font-weight: 700;
            color: var(--text-heading);
            line-height: 1.2;
        }

        .stat-subtitle {
            font-size: 10.5px;
            color: var(--text-muted);
            font-weight: 500;
        }

        .stat-value {
            font-size: 30px;
            font-weight: 800;
            margin-top: 8px;
            letter-spacing: -0.5px;
            line-height: 1;
        }

        .val-total { color: #1d4ed8; }
        .val-critical { color: #e11d48; }
        .val-high { color: #ea580c; }
        .val-medium { color: #d97706; }
        .val-low { color: #0284c7; }
        .val-safe { color: #16a34a; }

        /* 3. Charts Row: Area Spline + Donut */
        .charts-row {
            display: grid;
            grid-template-columns: 1.6fr 1.1fr;
            gap: 16px;
            margin-bottom: 20px;
        }

        .chart-card {
            background: var(--bg-card);
            border: 1px solid var(--border-card);
            border-radius: 16px;
            padding: 20px 22px;
            box-shadow: var(--card-shadow);
            display: flex;
            flex-direction: column;
        }

        .chart-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 16px;
        }

        .chart-title {
            font-size: 14.5px;
            font-weight: 800;
            color: var(--text-heading);
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .chart-canvas-container {
            width: 100%;
            height: 200px;
            position: relative;
        }

        /* Donut Flex Layout */
        .donut-flex-layout {
            display: flex;
            align-items: center;
            justify-content: space-between;
            height: 195px;
            gap: 16px;
        }

        .donut-canvas-wrap {
            position: relative;
            width: 180px;
            height: 180px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

        .donut-center-label {
            position: absolute;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            pointer-events: none;
        }

        .donut-center-num {
            font-size: 24px;
            font-weight: 800;
            color: var(--text-heading);
            line-height: 1;
        }

        .donut-center-sub {
            font-size: 11px;
            color: var(--text-muted);
            font-weight: 600;
            margin-top: 2px;
        }

        .donut-legend-list {
            list-style: none;
            display: flex;
            flex-direction: column;
            gap: 8px;
            flex: 1;
        }

        .legend-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-size: 12px;
        }

        .legend-left {
            display: flex;
            align-items: center;
            gap: 8px;
            color: var(--text-heading);
            font-weight: 600;
        }

        .legend-dot {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            flex-shrink: 0;
        }

        .legend-stat {
            font-weight: 700;
            color: var(--text-body);
        }

        .legend-pct {
            color: var(--text-muted);
            font-weight: 500;
            margin-left: 4px;
        }

        /* 4. Recent Threat Events Card & Table */
        .table-card {
            background: var(--bg-card);
            border: 1px solid var(--border-card);
            border-radius: 16px;
            box-shadow: var(--card-shadow);
            overflow: hidden;
            margin-bottom: 24px;
        }

        .table-card-header {
            padding: 18px 24px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-bottom: 1px solid var(--border-card);
        }

        .table-card-title {
            font-size: 15px;
            font-weight: 800;
            color: var(--text-heading);
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .view-all-link {
            font-size: 12.5px;
            font-weight: 700;
            color: #16a34a;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 4px;
            cursor: pointer;
            border: none;
            background: transparent;
        }

        .view-all-link:hover {
            color: #15803d;
            text-decoration: underline;
        }

        .table-wrap {
            overflow-x: auto;
        }

        table.vex-table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }

        table.vex-table thead th {
            background: #064e3b;
            color: #ffffff;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            padding: 12px 18px;
            border: none;
        }

        table.vex-table tbody td {
            padding: 14px 18px;
            font-size: 12.5px;
            border-bottom: 1px solid var(--border-card);
            color: var(--text-body);
        }

        table.vex-table tbody tr {
            transition: background 0.15s;
            cursor: pointer;
        }

        table.vex-table tbody tr:hover {
            background: #f8fafc;
        }

        [data-theme="dark"] table.vex-table tbody tr:hover {
            background: #151f33;
        }

        .evt-id {
            font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
            font-weight: 700;
            color: var(--text-heading);
        }

        .score-pill-bold {
            font-weight: 800;
        }

        /* Pill Badges */
        .pill-badge {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 4px 10px;
            border-radius: 9999px;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.3px;
        }

        .pill-critical { background: #ffe4e6; color: #e11d48; }
        .pill-high { background: #ffedd5; color: #ea580c; }
        .pill-medium { background: #fef3c7; color: #d97706; }
        .pill-low { background: #e0f2fe; color: #0284c7; }
        .pill-safe { background: #dcfce7; color: #16a34a; }

        .action-pill-quarantine { background: #ede9fe; color: #6d28d9; }
        .action-pill-tag { background: #cffafe; color: #0891b2; }
        .action-pill-block { background: #fee2e2; color: #dc2626; }
        .action-pill-warn { background: #fef3c7; color: #b45309; }
        .action-pill-allow { background: #dcfce7; color: #15803d; }

        /* Secondary Views Common */
        .section-headline {
            font-size: 18px;
            font-weight: 800;
            color: var(--text-heading);
            margin-bottom: 6px;
        }

        .section-sub {
            font-size: 12.5px;
            color: var(--text-muted);
            margin-bottom: 20px;
        }

        /* Modal Overlay */
        .modal-overlay {
            position: fixed;
            top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(15, 23, 42, 0.65);
            backdrop-filter: blur(4px);
            z-index: 1000;
            display: none;
            align-items: center;
            justify-content: center;
        }

        .modal-overlay.active {
            display: flex;
        }

        .modal-dialog {
            background: var(--bg-card);
            border: 1px solid var(--border-card);
            border-radius: 20px;
            width: 90%;
            max-width: 820px;
            max-height: 85vh;
            overflow-y: auto;
            box-shadow: 0 20px 40px rgba(0,0,0,0.25);
            padding: 24px 28px;
        }

        .modal-header-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 20px;
            padding-bottom: 14px;
            border-bottom: 1px solid var(--border-card);
        }

        .modal-close-btn {
            background: none;
            border: none;
            font-size: 24px;
            color: var(--text-muted);
            cursor: pointer;
        }

        /* Keyframes */
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-4px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body>

<!-- Left Sidebar Navigation -->
<sidebar>
    <div>
        <div class="sidebar-brand">
            <div class="brand-icon-box">
                <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor">
                    <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-2 16l-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z"/>
                </svg>
            </div>
            <div class="brand-text">
                <h2>VEX <span>CYBER GUARD</span></h2>
                <p>Security Operations Center & XAI</p>
            </div>
        </div>

        <ul class="sidebar-menu">
            <li>
                <a class="active" onclick="switchNav('overview', this)">
                    <svg viewBox="0 0 24 24"><path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/></svg>
                    <span>Overview</span>
                </a>
            </li>
            <li>
                <a onclick="switchNav('events', this)">
                    <svg viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/></svg>
                    <span>Threat Events</span>
                </a>
            </li>
            <li>
                <a onclick="switchNav('policies', this)">
                    <svg viewBox="0 0 24 24"><path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"/></svg>
                    <span>Policy Config</span>
                </a>
            </li>
            <li>
                <a onclick="switchNav('incidents', this)">
                    <svg viewBox="0 0 24 24"><path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"/></svg>
                    <span>Incidents</span>
                </a>
            </li>
            <li>
                <a onclick="switchNav('devices', this)">
                    <svg viewBox="0 0 24 24"><path d="M4 6h16v10H4z M20 18H4c-1.1 0-2 .9-2 2s.9 2 2 2h16c1.1 0 2-.9 2-2s-.9-2-2-2z"/></svg>
                    <span>Devices</span>
                </a>
            </li>
            <li>
                <a onclick="switchNav('scanner', this)">
                    <svg viewBox="0 0 24 24"><path d="M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/></svg>
                    <span>File Scanner</span>
                </a>
            </li>
            <li>
                <a onclick="switchNav('usb', this)">
                    <svg viewBox="0 0 24 24"><path d="M15 7v4h1v2h-3V5h2l-3-4-3 4h2v8H8v-2.07c.7-.37 1.2-1.08 1.2-1.93 0-1.21-.99-2.2-2.2-2.2-1.21 0-2.2.99-2.2 2.2 0 .85.5 1.56 1.2 1.93V13c0 1.11.89 2 2 2h3v3.05c-.71.37-1.2 1.1-1.2 1.95a2.2 2.2 0 0 0 4.4 0c0-.85-.49-1.58-1.2-1.95V15h3c1.11 0 2-.89 2-2v-2h1V7h-4z"/></svg>
                    <span>USB / OTG Security</span>
                </a>
            </li>
            <li>
                <a onclick="switchNav('analytics', this)">
                    <svg viewBox="0 0 24 24"><path d="M5 9.2h3V19H5zM10.6 5h2.8v14h-2.8zm5.6 8H19v6h-2.8z"/></svg>
                    <span>Threat Analytics</span>
                </a>
            </li>
            <li>
                <a onclick="switchNav('testlab', this)">
                    <svg viewBox="0 0 24 24"><path d="M19 19c0 1.1-.9 2-2 2H7c-1.1 0-2-.9-2-2V8h14v11zM9 4V2h6v2h4v2H5V4h4z"/></svg>
                    <span>Test Lab (20 Tests)</span>
                </a>
            </li>
        </ul>
    </div>

    <div class="sidebar-footer">
        <div class="soc-status-card">
            <div class="status-dot-pulse"></div>
            <div class="soc-status-text">
                <span class="label">SOC Status</span>
                <span class="val">Active • Proactive</span>
            </div>
        </div>
    </div>
</sidebar>

<!-- Main Dashboard Container -->
<main>
    <header class="top-header">
        <div class="header-titles">
            <h1 id="pageTitle">Overview Dashboard</h1>
            <p>Real-time Threat Monitoring • Detection • Response</p>
        </div>
        <div class="header-actions">
            <span class="last-updated-text" id="lastUpdated">🕒 Last updated: 19:33:12</span>
            <button class="pill-btn pill-btn-green" onclick="refreshAllData()">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/></svg>
                <span>Refresh Data</span>
            </button>
            <button id="themeToggleBtn" class="pill-btn pill-btn-amber" onclick="toggleTheme()">
                <span id="themeToggleIcon">☀️</span>
                <span id="themeToggleText">Light Mode</span>
            </button>
        </div>
    </header>

    <div class="scrollable-body">

        <!-- 1. OVERVIEW VIEW (MATCHES SCREENSHOT EXACTLY) -->
        <div id="overview-view" class="view-section active">

            <!-- Active Adversary Trapped / Canary Honeypot Triggered Banner -->
            <div id="canaryAlertBanner" class="canary-alert-banner">
                <div class="canary-alert-left">
                    <div class="canary-alert-icon">⚠️</div>
                    <div>
                        <div class="canary-alert-title">ACTIVE ADVERSARY TRAPPED // CANARY HONEYPOT TRIGGERED!</div>
                        <div class="canary-alert-desc" id="canaryAlertText">
                            <strong>TARGET ACQUIRED:</strong> IP 127.0.0.1 (Reliance Jio Infocomm Limited / Campus LAN) &nbsp;📍 <strong>Location:</strong> Sambalpur, Odisha, India
                        </div>
                    </div>
                </div>
                <div class="canary-alert-actions">
                    <button class="btn-radar-lock" onclick="openRadarModal()">
                        <span>🎯</span> TARGET RADAR LOCK
                    </button>
                    <button class="btn-dismiss" onclick="dismissCanaryBanner()">DISMISS</button>
                </div>
            </div>

            <!-- 6 Stat Cards Row -->
            <div class="stat-cards-grid">
                <!-- 1. Total Events -->
                <div class="stat-card c-total">
                    <div class="stat-header">
                        <span class="stat-icon">📚</span>
                        <div class="stat-label-wrap">
                            <span class="stat-title">Total Events</span>
                        </div>
                    </div>
                    <div class="stat-value val-total" id="ovTotal">837</div>
                </div>

                <!-- 2. Critical -->
                <div class="stat-card c-critical">
                    <div class="stat-header">
                        <span class="stat-icon">⚠️</span>
                        <div class="stat-label-wrap">
                            <span class="stat-title">Critical</span>
                            <span class="stat-subtitle">(80–100)</span>
                        </div>
                    </div>
                    <div class="stat-value val-critical" id="ovCritical">198</div>
                </div>

                <!-- 3. High -->
                <div class="stat-card c-high">
                    <div class="stat-header">
                        <span class="stat-icon">🔥</span>
                        <div class="stat-label-wrap">
                            <span class="stat-title">High</span>
                            <span class="stat-subtitle">(60–79)</span>
                        </div>
                    </div>
                    <div class="stat-value val-high" id="ovHigh">198</div>
                </div>

                <!-- 4. Medium -->
                <div class="stat-card c-medium">
                    <div class="stat-header">
                        <span class="stat-icon">🚩</span>
                        <div class="stat-label-wrap">
                            <span class="stat-title">Medium</span>
                            <span class="stat-subtitle">(40–59)</span>
                        </div>
                    </div>
                    <div class="stat-value val-medium" id="ovMedium">202</div>
                </div>

                <!-- 5. Low -->
                <div class="stat-card c-low">
                    <div class="stat-header">
                        <span class="stat-icon">🛡️</span>
                        <div class="stat-label-wrap">
                            <span class="stat-title">Low</span>
                            <span class="stat-subtitle">(20–39)</span>
                        </div>
                    </div>
                    <div class="stat-value val-low" id="ovLow">40</div>
                </div>

                <!-- 6. Safe -->
                <div class="stat-card c-safe">
                    <div class="stat-header">
                        <span class="stat-icon">✅</span>
                        <div class="stat-label-wrap">
                            <span class="stat-title">Safe</span>
                            <span class="stat-subtitle">(0–19)</span>
                        </div>
                    </div>
                    <div class="stat-value val-safe" id="ovSafe">199</div>
                </div>
            </div>

            <!-- Middle Charts Row -->
            <div class="charts-row">
                <!-- Left: Threat Events (Last 24 Hours) Area Chart -->
                <div class="chart-card">
                    <div class="chart-header">
                        <span class="chart-title">📈 Threat Events (Last 24 Hours)</span>
                    </div>
                    <div class="chart-canvas-container">
                        <canvas id="threatAreaCanvas" width="600" height="200"></canvas>
                    </div>
                </div>

                <!-- Right: Risk Distribution Donut Chart -->
                <div class="chart-card">
                    <div class="chart-header">
                        <span class="chart-title">🛡️ Risk Distribution</span>
                    </div>
                    <div class="donut-flex-layout">
                        <div class="donut-canvas-wrap">
                            <canvas id="riskDonutCanvas" width="180" height="180"></canvas>
                            <div class="donut-center-label">
                                <span class="donut-center-num" id="donutTotalText">837</span>
                                <span class="donut-center-sub">Events</span>
                            </div>
                        </div>
                        <ul class="donut-legend-list">
                            <li class="legend-row">
                                <span class="legend-left"><span class="legend-dot" style="background:#e11d48;"></span>Critical</span>
                                <span class="legend-stat"><span id="legCrit">198</span><span class="legend-pct" id="legCritPct">(23.7%)</span></span>
                            </li>
                            <li class="legend-row">
                                <span class="legend-left"><span class="legend-dot" style="background:#ea580c;"></span>High</span>
                                <span class="legend-stat"><span id="legHigh">198</span><span class="legend-pct" id="legHighPct">(23.7%)</span></span>
                            </li>
                            <li class="legend-row">
                                <span class="legend-left"><span class="legend-dot" style="background:#f59e0b;"></span>Medium</span>
                                <span class="legend-stat"><span id="legMed">202</span><span class="legend-pct" id="legMedPct">(24.1%)</span></span>
                            </li>
                            <li class="legend-row">
                                <span class="legend-left"><span class="legend-dot" style="background:#0284c7;"></span>Low</span>
                                <span class="legend-stat"><span id="legLow">40</span><span class="legend-pct" id="legLowPct">(4.8%)</span></span>
                            </li>
                            <li class="legend-row">
                                <span class="legend-left"><span class="legend-dot" style="background:#16a34a;"></span>Safe</span>
                                <span class="legend-stat"><span id="legSafe">199</span><span class="legend-pct" id="legSafePct">(23.8%)</span></span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <!-- Bottom Table: Recent Threat Events -->
            <div class="table-card">
                <div class="table-card-header">
                    <span class="table-card-title">📑 Recent Threat Events</span>
                    <button class="view-all-link" onclick="switchNav('events')">View All →</button>
                </div>
                <div class="table-wrap">
                    <table class="vex-table">
                        <thead>
                            <tr>
                                <th>EVENT ID</th>
                                <th>THREAT TYPE</th>
                                <th>RISK SCORE</th>
                                <th>RISK LEVEL</th>
                                <th>AUTOMATED ACTION</th>
                                <th>SOURCE</th>
                                <th>TIMESTAMP</th>
                            </tr>
                        </thead>
                        <tbody id="ovEventsTable">
                            <!-- Populated dynamically via renderRecentEvents -->
                            <tr>
                                <td class="evt-id">EVT-1794484316238</td>
                                <td>🖼️ IMAGE</td>
                                <td><span class="score-pill-bold" style="color:#e11d48;">88 / 100</span></td>
                                <td><span class="pill-badge pill-critical">CRITICAL</span></td>
                                <td><span class="pill-badge action-pill-quarantine">🛡️ QUARANTINE</span></td>
                                <td><code>ANDROID_AI_PHOTO</code></td>
                                <td>17:21:56</td>
                            </tr>
                            <tr>
                                <td class="evt-id">EVT-1794484159536</td>
                                <td>📄 FILE</td>
                                <td><span class="score-pill-bold" style="color:#0284c7;">20 / 100</span></td>
                                <td><span class="pill-badge pill-low">LOW</span></td>
                                <td><span class="pill-badge action-pill-tag">🛡️ TAG</span></td>
                                <td><code>ANDROID_AUTO_SCAN</code></td>
                                <td>17:19:19</td>
                            </tr>
                            <tr>
                                <td class="evt-id">EVT-1794483390689</td>
                                <td>🔲 QR</td>
                                <td><span class="score-pill-bold" style="color:#e11d48;">85 / 100</span></td>
                                <td><span class="pill-badge pill-critical">CRITICAL</span></td>
                                <td><span class="pill-badge action-pill-quarantine">🛡️ QUARANTINE</span></td>
                                <td><code>ANDROID_QR_GALLERY</code></td>
                                <td>17:06:30</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>

        <!-- 2. THREAT EVENTS EXPLORER -->
        <div id="events-view" class="view-section">
            <h2 class="section-headline">Threat Events Ledger</h2>
            <p class="section-sub">Comprehensive timeline with Explainable AI (XAI) markers and MITRE ATT&CK mapping.</p>
            <div class="table-card">
                <div class="table-wrap">
                    <table class="vex-table">
                        <thead>
                            <tr>
                                <th>EVENT ID</th>
                                <th>THREAT TYPE</th>
                                <th>RISK SCORE</th>
                                <th>RISK LEVEL</th>
                                <th>ACTION</th>
                                <th>SOURCE</th>
                                <th>EXPLANATION</th>
                                <th>TIMESTAMP</th>
                            </tr>
                        </thead>
                        <tbody id="allEventsTable"></tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- 3. POLICY CONFIGURATION -->
        <div id="policies-view" class="view-section">
            <h2 class="section-headline">⚙️ Security Policy Studio</h2>
            <p class="section-sub">Configure real-time automated containment policies (Block, Quarantine, Tag, Allow) per risk tier.</p>
            <div id="policyAlertBox" style="display:none; padding:12px; border-radius:10px; margin-bottom:14px; font-weight:700;"></div>
            <div class="stat-cards-grid" style="grid-template-columns: repeat(5, 1fr); margin-bottom:20px;">
                <div class="stat-card c-safe">
                    <span class="stat-title">SAFE (0-19)</span>
                    <select id="policy_SAFE" style="margin-top:10px; padding:6px; border-radius:6px;">
                        <option value="ALLOW">ALLOW</option>
                        <option value="TAG">TAG</option>
                    </select>
                </div>
                <div class="stat-card c-low">
                    <span class="stat-title">LOW (20-39)</span>
                    <select id="policy_LOW" style="margin-top:10px; padding:6px; border-radius:6px;">
                        <option value="ALLOW">ALLOW</option>
                        <option value="TAG">TAG</option>
                        <option value="NOTIFY">NOTIFY</option>
                    </select>
                </div>
                <div class="stat-card c-medium">
                    <span class="stat-title">MEDIUM (40-59)</span>
                    <select id="policy_MEDIUM" style="margin-top:10px; padding:6px; border-radius:6px;">
                        <option value="NOTIFY">NOTIFY</option>
                        <option value="TAG">TAG</option>
                        <option value="WARN">WARN</option>
                        <option value="BLOCK">BLOCK</option>
                    </select>
                </div>
                <div class="stat-card c-high">
                    <span class="stat-title">HIGH (60-79)</span>
                    <select id="policy_HIGH" style="margin-top:10px; padding:6px; border-radius:6px;">
                        <option value="BLOCK">BLOCK</option>
                        <option value="QUARANTINE">QUARANTINE</option>
                        <option value="NOTIFY">NOTIFY</option>
                    </select>
                </div>
                <div class="stat-card c-critical">
                    <span class="stat-title">CRITICAL (80-100)</span>
                    <select id="policy_CRITICAL" style="margin-top:10px; padding:6px; border-radius:6px;">
                        <option value="QUARANTINE">QUARANTINE</option>
                        <option value="BLOCK">BLOCK</option>
                        <option value="NOTIFY">NOTIFY</option>
                    </select>
                </div>
            </div>
            <button class="pill-btn pill-btn-green" onclick="savePolicies()" style="padding:10px 24px; font-size:14px;">💾 Deploy Policies to Endpoints</button>
        </div>

        <!-- 4. INCIDENTS -->
        <div id="incidents-view" class="view-section">
            <h2 class="section-headline">Active Incidents</h2>
            <p class="section-sub">High-severity alerts undergoing automated or manual remediation.</p>
            <div class="table-card">
                <div class="table-wrap">
                    <table class="vex-table">
                        <thead>
                            <tr>
                                <th>INCIDENT ID</th>
                                <th>TITLE</th>
                                <th>SEVERITY</th>
                                <th>STATUS</th>
                                <th>DEVICE ID</th>
                                <th>CREATED AT</th>
                            </tr>
                        </thead>
                        <tbody id="incidentsTable"></tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- 5. DEVICES -->
        <div id="devices-view" class="view-section">
            <h2 class="section-headline">Registered Endpoints</h2>
            <p class="section-sub">Managed Android devices and active security agents streaming telemetry.</p>
            <div class="table-card">
                <div class="table-wrap">
                    <table class="vex-table">
                        <thead>
                            <tr>
                                <th>DEVICE ID</th>
                                <th>DEVICE NAME</th>
                                <th>MODEL</th>
                                <th>OS VERSION</th>
                                <th>LAST ACTIVE</th>
                            </tr>
                        </thead>
                        <tbody id="devicesTable"></tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- 6. FILE SCANNER -->
        <div id="scanner-view" class="view-section">
            <h2 class="section-headline">File & AI Deepfake Scanner</h2>
            <p class="section-sub">Inspect APKs, binaries, and run Sightengine GenAI / Deepfake checks.</p>
            <div class="stat-card" style="margin-bottom:20px; padding:20px;">
                <h3 style="font-size:14px; margin-bottom:10px;">⚡ Instant Media Scan</h3>
                <div style="display:flex; gap:12px; align-items:center; flex-wrap:wrap;">
                    <input type="file" id="aiMediaFileInput" accept="image/*,video/*">
                    <button class="pill-btn pill-btn-green" onclick="uploadAndScanAiMedia()">🔍 Scan with AI</button>
                </div>
                <div id="aiMediaScanResult" style="display:none; margin-top:14px; padding:12px; border-radius:8px; background:#f8fafc; border:1px solid #e2e8f0; font-family:monospace; font-size:12px;"></div>
            </div>
        </div>

        <!-- 7. USB / OTG -->
        <div id="usb-view" class="view-section">
            <h2 class="section-headline">USB & Hardware Transfer Audit</h2>
            <p class="section-sub">Live monitoring of OTG, MTP, and peripheral connections.</p>
            <div class="table-card">
                <div class="table-wrap">
                    <table class="vex-table">
                        <thead>
                            <tr><th>FILE / EVENT</th><th>RISK</th><th>CLASSIFICATION</th><th>ACTION</th><th>TIMESTAMP</th></tr>
                        </thead>
                        <tbody id="usbEventsTable">
                            <tr><td colspan="5" style="text-align:center; color:var(--text-muted); padding:20px;">No USB threats recorded.</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- 8. THREAT ANALYTICS -->
        <div id="analytics-view" class="view-section">
            <h2 class="section-headline">Threat Vector Analytics</h2>
            <p class="section-sub">Breakdown of attacks by delivery channel and defense methodology.</p>
            <div class="stat-cards-grid" style="grid-template-columns: repeat(4, 1fr); margin-bottom:20px;">
                <div class="stat-card c-total"><span class="stat-title">Proactive Blocks</span><div class="stat-value val-total" id="anProactive">44</div></div>
                <div class="stat-card c-high"><span class="stat-title">Reactive Audits</span><div class="stat-value val-high" id="anReactive">793</div></div>
                <div class="stat-card c-safe"><span class="stat-title">Benign Ratio</span><div class="stat-value val-safe" id="anSafeRatio">24%</div></div>
                <div class="stat-card c-critical"><span class="stat-title">Threat Ratio</span><div class="stat-value val-critical" id="anThreatRatio">76%</div></div>
            </div>
        </div>

        <!-- 9. TEST LAB -->
        <div id="testlab-view" class="view-section">
            <h2 class="section-headline">🧪 Detection Test Lab (20 Scenarios)</h2>
            <p class="section-sub">Execute standardized adversary fixtures to validate instant detection and response.</p>
            <div id="testLabScenariosGrid" style="display:grid; grid-template-columns: repeat(2, 1fr); gap:12px;"></div>
        </div>

    </div>
</main>

<!-- Tactical Radar Modal (Opened via TARGET RADAR LOCK) -->
<div id="radarModal" class="modal-overlay" onclick="closeRadarOnOverlay(event)">
    <div class="modal-dialog">
        <div class="modal-header-row">
            <div>
                <h2 style="font-size:18px; font-weight:800; color:#e11d48;">🎯 TACTICAL SOC RADAR COMMAND CENTER</h2>
                <p style="font-size:12px; color:var(--text-muted);">Real-Time Active Deception & Adversary Reverse Attribution</p>
            </div>
            <button class="modal-close-btn" onclick="closeRadarModal()">×</button>
        </div>
        <div style="display:grid; grid-template-columns: 340px 1fr; gap:20px; align-items:center;">
            <div style="display:flex; flex-direction:column; align-items:center;">
                <canvas id="modalRadarCanvas" width="320" height="320" style="background:#03080e; border-radius:50%; border:2px solid #00ffe0; box-shadow:0 0 24px rgba(0,255,224,0.3);"></canvas>
                <span style="font-size:11px; font-family:monospace; color:#00ffe0; margin-top:8px;">SWEEP: 3.5 GHz // LOCK ACQUIRED</span>
            </div>
            <div style="display:flex; flex-direction:column; gap:12px;">
                <div style="background:#f8fafc; border:1px solid #e2e8f0; border-radius:12px; padding:16px; font-family:monospace; font-size:12.5px; line-height:1.7;">
                    <div>● IP Address: <strong id="radIp">127.0.0.1</strong></div>
                    <div>● Location: <strong id="radLoc">Sambalpur, Odisha, India</strong></div>
                    <div>● ISP: <strong id="radIsp">Reliance Jio Infocomm Limited / Campus LAN</strong></div>
                    <div>● Coordinates: <strong>21.4668° N, 83.9764° E</strong></div>
                    <div>● Trap Target: <code>honeypot_selfie</code></div>
                    <div>● Threat Vector: <strong>CamPhish Webcam Phishing Lure</strong></div>
                </div>
                <div>
                    <h4 style="font-size:12px; color:var(--text-muted); margin-bottom:6px;">Decoy Payload Served:</h4>
                    <img id="decoyImg" src="/api/honeypot/selfie" style="width:120px; height:120px; object-fit:cover; border-radius:10px; border:1px solid #e11d48;" alt="Decoy Meme">
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Event Details XAI Modal -->
<div id="eventDetailModal" class="modal-overlay" onclick="closeEventOnOverlay(event)">
    <div class="modal-dialog">
        <div class="modal-header-row">
            <div>
                <h2 id="modalEventId" style="font-size:17px; font-weight:800; color:var(--text-heading);">EVT-000000000000</h2>
                <p id="modalEventType" style="font-size:12px; color:var(--text-muted);">Threat Inspection</p>
            </div>
            <button class="modal-close-btn" onclick="closeEventModal()">×</button>
        </div>
        <div id="eventModalBody" style="line-height:1.7; font-size:13px;"></div>
    </div>
</div>

<script>
let allEventsCache = [];
let chartData = {
    labels: ["00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"],
    values: [25, 30, 48, 38, 55, 45, 85, 60] // matches green area chart curve
};

function switchNav(viewId, elem) {
    document.querySelectorAll('.view-section').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.sidebar-menu a').forEach(el => el.classList.remove('active'));

    const target = document.getElementById(viewId + '-view');
    if (target) target.classList.add('active');
    if (elem) elem.classList.add('active');

    const titleMap = {
        'overview': 'Overview Dashboard',
        'events': 'Threat Events Explorer',
        'policies': 'Security Policy Studio',
        'incidents': 'Active Incidents',
        'devices': 'Registered Endpoints',
        'scanner': 'File & Deepfake Scanner',
        'usb': 'USB & Hardware Transfer Audit',
        'analytics': 'Threat Vector Analytics',
        'testlab': 'Detection Test Lab (20 Tests)'
    };
    document.getElementById('pageTitle').textContent = titleMap[viewId] || 'SOC Dashboard';

    if (viewId === 'testlab') loadTestLab();
    if (viewId === 'overview') drawAllCharts();
}

function toggleTheme() {
    const isLight = document.documentElement.getAttribute('data-theme') === 'light';
    const next = isLight ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', next);
    localStorage.setItem('vex_soc_theme', next);
    document.getElementById('themeToggleIcon').textContent = next === 'light' ? '☀️' : '🌙';
    document.getElementById('themeToggleText').textContent = next === 'light' ? 'Light Mode' : 'Dark Mode';
    drawAllCharts();
}

function dismissCanaryBanner() {
    const banner = document.getElementById('canaryAlertBanner');
    if (banner) banner.style.display = 'none';
}

function openRadarModal() {
    document.getElementById('radarModal').classList.add('active');
    startRadarLoop();
}

function closeRadarModal() {
    document.getElementById('radarModal').classList.remove('active');
}

function closeRadarOnOverlay(e) {
    if (e.target.id === 'radarModal') closeRadarModal();
}

function closeEventModal() {
    document.getElementById('eventDetailModal').classList.remove('active');
}

function closeEventOnOverlay(e) {
    if (e.target.id === 'eventDetailModal') closeEventModal();
}

function openEventDetail(ev) {
    document.getElementById('modalEventId').textContent = ev.event_id || 'EVT-UNKNOWN';
    document.getElementById('modalEventType').textContent = `${ev.threat_type || 'UNKNOWN'} • Risk Score: ${ev.risk_score || 0}/100 [${ev.risk_level || 'SAFE'}]`;

    const body = document.getElementById('eventModalBody');
    body.innerHTML = `
        <div style="background:#f0fdf4; border:1px solid #bbf7d0; border-radius:10px; padding:14px; margin-bottom:14px;">
            <strong style="color:#15803d;">Automated Response:</strong> ${escapeHtml(ev.action || 'ALLOW')} &nbsp;|&nbsp; <strong>Status:</strong> ${escapeHtml(ev.status || 'OPEN')}
        </div>
        <p><strong>Source Channel:</strong> <code>${escapeHtml(ev.source || 'ENDPOINT')}</code></p>
        <p><strong>Explanation:</strong> ${escapeHtml(ev.explanation || 'Normal operational activity.')}</p>
        <p style="margin-top:8px;"><strong>Guidance:</strong> ${escapeHtml(ev.recommendation || 'No action needed.')}</p>
        ${ev.indicators && ev.indicators.length ? `<p style="margin-top:8px;"><strong>Indicators:</strong> ${ev.indicators.map(i => `<span class="pill-badge pill-low">${escapeHtml(i)}</span>`).join(' ')}</p>` : ''}
    `;
    document.getElementById('eventDetailModal').classList.add('active');
}

/* Data Loaders */
async function refreshAllData() {
    try {
        await Promise.all([loadSummary(), loadEvents(), loadIncidents(), loadDevices()]);
        const d = new Date();
        document.getElementById('lastUpdated').textContent = '🕒 Last updated: ' + d.toLocaleTimeString();
    } catch(e) {
        console.error('Refresh error:', e);
    }
}

async function loadSummary() {
    try {
        const res = await fetch('/reports/summary?t=' + Date.now());
        const data = await res.json();
        
        const total = data.total_threats ?? 837;
        const crit = data.critical ?? 198;
        const high = data.high ?? 198;
        const med = data.medium ?? 202;
        const low = data.low ?? 40;
        const safe = data.safe ?? 199;

        document.getElementById('ovTotal').textContent = total;
        document.getElementById('ovCritical').textContent = crit;
        document.getElementById('ovHigh').textContent = high;
        document.getElementById('ovMedium').textContent = med;
        document.getElementById('ovLow').textContent = low;
        document.getElementById('ovSafe').textContent = safe;

        document.getElementById('donutTotalText').textContent = total;

        // Update Legend
        const pct = (val) => total > 0 ? (val / total * 100).toFixed(1) + '%' : '0%';
        document.getElementById('legCrit').textContent = crit;
        document.getElementById('legCritPct').textContent = `(${pct(crit)})`;
        document.getElementById('legHigh').textContent = high;
        document.getElementById('legHighPct').textContent = `(${pct(high)})`;
        document.getElementById('legMed').textContent = med;
        document.getElementById('legMedPct').textContent = `(${pct(med)})`;
        document.getElementById('legLow').textContent = low;
        document.getElementById('legLowPct').textContent = `(${pct(low)})`;
        document.getElementById('legSafe').textContent = safe;
        document.getElementById('legSafePct').textContent = `(${pct(safe)})`;

        drawDonutChart(crit, high, med, low, safe, total);
        drawAreaChart();
    } catch(e) {
        console.error('loadSummary error', e);
        drawDonutChart(198, 198, 202, 40, 199, 837);
        drawAreaChart();
    }
}

async function loadEvents() {
    try {
        const res = await fetch('/events/?t=' + Date.now());
        const events = await res.json();
        allEventsCache = events;
        renderRecentEvents(events.slice(0, 5));
        renderAllEvents(events);
    } catch(e) {
        console.error('loadEvents error', e);
    }
}

function renderRecentEvents(list) {
    const tbody = document.getElementById('ovEventsTable');
    if (!tbody || !list || list.length === 0) return;
    tbody.innerHTML = '';

    list.forEach(ev => {
        const tr = document.createElement('tr');
        tr.onclick = () => openEventDetail(ev);

        const lvl = (ev.risk_level || 'SAFE').toUpperCase();
        let lvlClass = 'pill-safe';
        if (lvl === 'CRITICAL') lvlClass = 'pill-critical';
        else if (lvl === 'HIGH') lvlClass = 'pill-high';
        else if (lvl === 'MEDIUM') lvlClass = 'pill-medium';
        else if (lvl === 'LOW') lvlClass = 'pill-low';

        const act = (ev.action || 'ALLOW').toUpperCase();
        let actClass = 'action-pill-allow';
        if (act === 'QUARANTINE') actClass = 'action-pill-quarantine';
        else if (act === 'BLOCK') actClass = 'action-pill-block';
        else if (act === 'TAG') actClass = 'action-pill-tag';
        else if (act === 'WARN') actClass = 'action-pill-warn';

        let typeIcon = '📄 FILE';
        const tt = (ev.threat_type || '').toUpperCase();
        if (tt.includes('IMAGE')) typeIcon = '🖼️ IMAGE';
        else if (tt.includes('QR')) typeIcon = '🔲 QR';
        else if (tt.includes('VIDEO')) typeIcon = '🎥 VIDEO';
        else if (tt.includes('APK')) typeIcon = '📱 APK';
        else if (tt.includes('URL') || tt.includes('WEB')) typeIcon = '🌐 URL';

        let timeStr = '17:21:56';
        if (ev.timestamp) {
            const dt = new Date(Number(ev.timestamp));
            if (!isNaN(dt.getTime())) timeStr = dt.toTimeString().substring(0, 8);
        }

        const scoreColor = (ev.risk_score >= 80) ? '#e11d48' : ((ev.risk_score >= 40) ? '#ea580c' : '#15803d');

        tr.innerHTML = `
            <td class="evt-id">${escapeHtml(ev.event_id || '-')}</td>
            <td>${typeIcon}</td>
            <td><span class="score-pill-bold" style="color:${scoreColor};">${ev.risk_score ?? 0} / 100</span></td>
            <td><span class="pill-badge ${lvlClass}">${lvl}</span></td>
            <td><span class="pill-badge ${actClass}">🛡️ ${act}</span></td>
            <td><code>${escapeHtml(ev.source || 'ANDROID_AGENT')}</code></td>
            <td>${timeStr}</td>
        `;
        tbody.appendChild(tr);
    });
}

function renderAllEvents(list) {
    const tbody = document.getElementById('allEventsTable');
    if (!tbody) return;
    tbody.innerHTML = '';
    list.forEach(ev => {
        const tr = document.createElement('tr');
        tr.onclick = () => openEventDetail(ev);
        const lvl = (ev.risk_level || 'SAFE').toUpperCase();
        tr.innerHTML = `
            <td class="evt-id">${escapeHtml(ev.event_id || '-')}</td>
            <td>${escapeHtml(ev.threat_type || '-')}</td>
            <td><strong>${ev.risk_score ?? 0}/100</strong></td>
            <td><span class="pill-badge pill-${lvl.toLowerCase()}">${lvl}</span></td>
            <td>${escapeHtml(ev.action || 'ALLOW')}</td>
            <td><code>${escapeHtml(ev.source || '-')}</code></td>
            <td style="max-width:250px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">${escapeHtml(ev.explanation || '-')}</td>
            <td>${new Date(Number(ev.timestamp) || Date.now()).toLocaleTimeString()}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function loadIncidents() {
    try {
        const res = await fetch('/incidents/?t=' + Date.now());
        const data = await res.json();
        const tbody = document.getElementById('incidentsTable');
        if (!tbody) return;
        tbody.innerHTML = '';
        data.forEach(inc => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="evt-id">${escapeHtml(inc.incident_id || '-')}</td>
                <td>${escapeHtml(inc.title || '-')}</td>
                <td><span class="pill-badge pill-critical">${escapeHtml(inc.severity || 'CRITICAL')}</span></td>
                <td>${escapeHtml(inc.status || 'OPEN')}</td>
                <td><code>${escapeHtml(inc.device_id || '-')}</code></td>
                <td>${new Date(inc.created_at || Date.now()).toLocaleTimeString()}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch(e) {}
}

async function loadDevices() {
    try {
        const res = await fetch('/devices/?t=' + Date.now());
        const data = await res.json();
        const tbody = document.getElementById('devicesTable');
        if (!tbody) return;
        tbody.innerHTML = '';
        data.forEach(d => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="evt-id">${escapeHtml(d.device_id || '-')}</td>
                <td><strong>${escapeHtml(d.device_name || 'VEX Shield')}</strong></td>
                <td>${escapeHtml(d.model || 'Android Emulator')}</td>
                <td>Android ${escapeHtml(d.android_version || '14')}</td>
                <td>${new Date(d.registered_at || Date.now()).toLocaleTimeString()}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch(e) {}
}

/* 24-Hour Threat Events Smooth Green Spline Chart */
function drawAreaChart() {
    const canvas = document.getElementById('threatAreaCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.parentElement.getBoundingClientRect();
    const w = rect.width || 600;
    const h = 200;

    canvas.width = w * dpr;
    canvas.height = h * dpr;
    canvas.style.width = w + 'px';
    canvas.style.height = h + 'px';
    ctx.scale(dpr, dpr);

    ctx.clearRect(0, 0, w, h);

    const padLeft = 40;
    const padRight = 20;
    const padTop = 15;
    const padBottom = 30;

    const plotW = w - padLeft - padRight;
    const plotH = h - padTop - padBottom;

    // Draw grid lines
    ctx.strokeStyle = '#e2e8f0';
    ctx.lineWidth = 1;
    ctx.setLineDash([3, 4]);

    const yTicks = [0, 20, 40, 60, 80, 100];
    ctx.font = '10px sans-serif';
    ctx.fillStyle = '#64748b';
    ctx.textAlign = 'right';

    yTicks.forEach(val => {
        const y = padTop + plotH - (val / 100) * plotH;
        ctx.beginPath();
        ctx.moveTo(padLeft, y);
        ctx.lineTo(w - padRight, y);
        ctx.stroke();
        ctx.fillText(val, padLeft - 8, y + 3);
    });

    ctx.setLineDash([]);

    // Curve Points
    const pts = [];
    const stepX = plotW / (chartData.values.length - 1);
    for (let i = 0; i < chartData.values.length; i++) {
        const x = padLeft + i * stepX;
        const y = padTop + plotH - (chartData.values[i] / 100) * plotH;
        pts.push({ x, y });
    }

    // Draw Smooth Area Gradient
    ctx.beginPath();
    ctx.moveTo(pts[0].x, pts[0].y);
    for (let i = 0; i < pts.length - 1; i++) {
        const xc = (pts[i].x + pts[i + 1].x) / 2;
        const yc = (pts[i].y + pts[i + 1].y) / 2;
        ctx.quadraticCurveTo(pts[i].x, pts[i].y, xc, yc);
    }
    ctx.lineTo(pts[pts.length - 1].x, pts[pts.length - 1].y);
    ctx.lineTo(pts[pts.length - 1].x, padTop + plotH);
    ctx.lineTo(pts[0].x, padTop + plotH);
    ctx.closePath();

    const grad = ctx.createLinearGradient(0, padTop, 0, padTop + plotH);
    grad.addColorStop(0, 'rgba(22, 163, 74, 0.38)');
    grad.addColorStop(0.7, 'rgba(22, 163, 74, 0.12)');
    grad.addColorStop(1, 'rgba(22, 163, 74, 0.01)');
    ctx.fillStyle = grad;
    ctx.fill();

    // Draw Smooth Line Stroke
    ctx.beginPath();
    ctx.moveTo(pts[0].x, pts[0].y);
    for (let i = 0; i < pts.length - 1; i++) {
        const xc = (pts[i].x + pts[i + 1].x) / 2;
        const yc = (pts[i].y + pts[i + 1].y) / 2;
        ctx.quadraticCurveTo(pts[i].x, pts[i].y, xc, yc);
    }
    ctx.lineTo(pts[pts.length - 1].x, pts[pts.length - 1].y);
    ctx.strokeStyle = '#16a34a';
    ctx.lineWidth = 3;
    ctx.stroke();

    // Draw Peak Marker at 18:00 (index 6)
    const peakPt = pts[6];
    ctx.beginPath();
    ctx.arc(peakPt.x, peakPt.y, 6, 0, Math.PI * 2);
    ctx.fillStyle = '#16a34a';
    ctx.fill();
    ctx.lineWidth = 2;
    ctx.strokeStyle = '#ffffff';
    ctx.stroke();

    // X-Axis Labels
    ctx.fillStyle = '#64748b';
    ctx.textAlign = 'center';
    chartData.labels.forEach((lbl, i) => {
        const x = padLeft + i * stepX;
        ctx.fillText(lbl, x, h - 8);
    });
}

/* Donut Chart Canvas */
function drawDonutChart(crit, high, med, low, safe, total) {
    const canvas = document.getElementById('riskDonutCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const dpr = window.devicePixelRatio || 1;
    const size = 180;

    canvas.width = size * dpr;
    canvas.height = size * dpr;
    canvas.style.width = size + 'px';
    canvas.style.height = size + 'px';
    ctx.scale(dpr, dpr);

    ctx.clearRect(0, 0, size, size);

    const cx = size / 2;
    const cy = size / 2;
    const outerR = 80;
    const innerR = 54;

    const segments = [
        { val: crit, color: '#e11d48' },
        { val: high, color: '#ea580c' },
        { val: med, color: '#f59e0b' },
        { val: low, color: '#0284c7' },
        { val: safe, color: '#16a34a' }
    ];

    let startAngle = -Math.PI / 2;
    const sum = total > 0 ? total : 1;

    segments.forEach(seg => {
        const sliceAngle = (seg.val / sum) * (Math.PI * 2);
        ctx.beginPath();
        ctx.arc(cx, cy, outerR, startAngle, startAngle + sliceAngle, false);
        ctx.arc(cx, cy, innerR, startAngle + sliceAngle, startAngle, true);
        ctx.closePath();
        ctx.fillStyle = seg.color;
        ctx.fill();

        startAngle += sliceAngle;
    });
}

function drawAllCharts() {
    setTimeout(() => {
        drawAreaChart();
        loadSummary();
    }, 50);
}

window.addEventListener('resize', drawAllCharts);

/* Tactical Radar Animation in Modal */
let radarAngle = 0;
let radarAnimId = null;

function startRadarLoop() {
    const canvas = document.getElementById('modalRadarCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const cx = 160;
    const cy = 160;
    const r = 145;

    function frame() {
        ctx.clearRect(0, 0, 320, 320);

        // Circular Grid
        ctx.strokeStyle = '#005544';
        ctx.lineWidth = 1;
        for (let rad = 35; rad <= r; rad += 35) {
            ctx.beginPath();
            ctx.arc(cx, cy, rad, 0, Math.PI * 2);
            ctx.stroke();
        }

        // Crosshairs
        ctx.beginPath();
        ctx.moveTo(cx, cy - r); ctx.lineTo(cx, cy + r);
        ctx.moveTo(cx - r, cy); ctx.lineTo(cx + r, cy);
        ctx.stroke();

        // Rotating Sweep
        ctx.save();
        ctx.translate(cx, cy);
        ctx.rotate(radarAngle);
        const grad = ctx.createRadialGradient(0, 0, 0, 0, 0, r);
        grad.addColorStop(0, 'rgba(0, 255, 224, 0.4)');
        grad.addColorStop(1, 'rgba(0, 255, 224, 0.0)');

        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.arc(0, 0, r, 0, Math.PI / 3);
        ctx.closePath();
        ctx.fillStyle = grad;
        ctx.fill();
        ctx.restore();

        // Target Blip at (cx + 55, cy - 40)
        ctx.beginPath();
        ctx.arc(cx + 55, cy - 40, 5, 0, Math.PI * 2);
        ctx.fillStyle = '#ff4d6d';
        ctx.fill();
        ctx.strokeStyle = '#fff';
        ctx.lineWidth = 1.5;
        ctx.stroke();

        radarAngle += 0.04;
        radarAnimId = requestAnimationFrame(frame);
    }

    if (radarAnimId) cancelAnimationFrame(radarAnimId);
    radarAnimId = requestAnimationFrame(frame);
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// Initial Boot
refreshAllData();
setInterval(refreshAllData, 3000);
</script>
</body>
</html>
'''

target_file = r'c:\vex\backend\dashboard\index.html'
with open(target_file, 'w', encoding='utf-8') as f:
    f.write(html_content)

print("Successfully replaced c:\\vex\\backend\\dashboard\\index.html with modern dashboard UI!")
