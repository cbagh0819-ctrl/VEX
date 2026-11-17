import React, { useState } from 'react';
import { asset } from '../utils/assets';
import { 
  Calendar, 
  MapPin, 
  Clock, 
  Award, 
  AlertTriangle, 
  CheckCircle2, 
  Train, 
  Flame, 
  Laptop, 
  WifiOff, 
  Sparkles, 
  ChevronRight, 
  ChevronLeft, 
  X, 
  Maximize2, 
  Camera, 
  Heart,
  Key,
  ShieldCheck,
  Compass
} from 'lucide-react';
import { sounds } from '../utils/audio';

interface PhotoItem {
  src: string;
  caption: string;
  timestamp: string;
  tag: string;
}

interface Chapter {
  id: number;
  dayLabel: string;
  dateStr: string;
  timeRange: string;
  title: string;
  subtitle: string;
  location: string;
  statusBadge: {
    text: string;
    type: 'gold' | 'blue' | 'red' | 'emerald' | 'amber';
  };
  storyParagraphs: string[];
  keyHighlight?: {
    title: string;
    description: string;
    badge: string;
    icon: string;
  };
  photos: PhotoItem[];
}

const JOURNEY_CHAPTERS: Chapter[] = [
  {
    id: 1,
    dayLabel: 'Day 1',
    dateStr: '11 November 2026',
    timeRange: '06:00 AM – 11:59 PM',
    title: 'The MEMO Train & Midnight Stops Across Odisha',
    subtitle: 'From Jagannathpur to Rourkela via Bhubaneswar',
    location: 'Jagannathpur ➔ Bhubaneswar ➔ Rourkela Intercity Express',
    statusBadge: {
      text: 'THE EXPEDITION BEGINS',
      type: 'blue'
    },
    storyParagraphs: [
      'Our journey commenced in the crisp early morning hours of November 11. We boarded the local MEMO train from Jagannathpur station heading towards Bhubaneswar, buzzing with anticipation for the biggest state hackathon of our academic lives.',
      'Reaching Bhubaneswar by late morning, we had breakfast and spent a few relaxing hours strolling through Reliance Mall near the railway station while waiting for our connecting train.',
      'At 2:00 PM, we boarded the Rourkela Intercity Express. The journey turned out to be an endurance test of its own: the train ran late and experienced repeated halts along remote tracks. Whenever the train halted in the dark Odisha countryside, we hopped down onto the gravel tracks under the starlight to stretch, breathe the cool air, and capture memories of an unforgettable night on the rails.'
    ],
    keyHighlight: {
      title: 'Midnight Trackside Halts',
      description: 'Stretching under the open sky past 11:00 PM as the Rourkela Intercity crept slowly toward Sundargarh.',
      badge: 'TRAVEL LOG',
      icon: 'Train'
    },
    photos: [
      {
        src: '/images/journey/ch1_bbsr_mall.jpg',
        caption: 'Breakfast & relaxing at Reliance Mall near Bhubaneswar Railway Station',
        timestamp: '11 Nov 2026 • 01:18 PM',
        tag: 'Bhubaneswar'
      },
      {
        src: '/images/journey/ch1_train_stop_evening.jpg',
        caption: 'Evening train halt along the scenic route toward Rourkela',
        timestamp: '11 Nov 2026 • 06:58 PM',
        tag: 'En Route'
      },
      {
        src: '/images/journey/ch1_train_night_track.jpg',
        caption: 'Stepping down onto the dark tracks during midnight halts at 10:50 PM',
        timestamp: '11 Nov 2026 • 10:50 PM',
        tag: 'Railway Tracks'
      },
      {
        src: '/images/journey/ch1_train_midnight.jpg',
        caption: 'Pushing deep into the night past 11:19 PM, tired yet determined',
        timestamp: '11 Nov 2026 • 11:19 PM',
        tag: 'Intercity Express'
      }
    ]
  },
  {
    id: 2,
    dayLabel: 'Day 2 (Night)',
    dateStr: '12 November 2026',
    timeRange: '01:15 AM – 03:00 AM',
    title: 'Midnight Rourkela Arrival, 2 AM Canteen & Mahanadi Hostel',
    subtitle: 'Campus Pickup, Midnight Dinner & Brotherhood in Room Allotment',
    location: 'Rourkela Station ➔ BPUT Campus Canteen ➔ Mahanadi Hostel',
    statusBadge: {
      text: 'ARRIVAL & HOSTEL SETUP',
      type: 'emerald'
    },
    storyParagraphs: [
      'We finally pulled into Rourkela Railway Station between 1:00 AM and 1:30 AM in the freezing November chill. Right outside the station, the dedicated BPUT Hackathon pickup bus was waiting to welcome arriving teams.',
      'The bus transported us straight through the campus gates to the canteen, where warm dinner was served at 2:00 AM. After a grueling 12-hour journey, that midnight meal was pure bliss.',
      'We then proceeded to Mahanadi Hostel to collect our room keys. In a fascinating twist, another team from our own college (PMEC) was also stationed there, competing for the exact same problem statement! Although Ankit had officially been allotted a different room down the corridor, we refused to split up: we pulled him straight into our room so Team VEX LOG could strategize side by side. We finally collapsed into bed at 3:00 AM, setting alarms for 7:00 AM.'
    ],
    keyHighlight: {
      title: 'Brotherhood First',
      description: 'Reuniting Ankit into our hostel room despite separate allotments to keep the squad unified.',
      badge: 'MAHANADI HOSTEL',
      icon: 'Key'
    },
    photos: [
      {
        src: '/images/journey/ch2_rourkela_arrival.jpg',
        caption: 'Stepping off the train at Rourkela Junction at 1:14 AM to meet the BPUT bus',
        timestamp: '12 Nov 2026 • 01:14 AM',
        tag: 'Rourkela Junction'
      },
      {
        src: '/images/journey/ch2_campus_canteen_2am.jpg',
        caption: 'Warm 2:00 AM late-night dinner at BPUT Campus Canteen after 12h travel',
        timestamp: '12 Nov 2026 • 02:06 AM',
        tag: 'Campus Canteen'
      },
      {
        src: '/images/journey/ch2_mahanadi_hostel.jpg',
        caption: 'Unpacking and setting up camp inside Mahanadi Hostel at 2:06 AM',
        timestamp: '12 Nov 2026 • 02:06 AM',
        tag: 'Mahanadi Hostel'
      }
    ]
  },
  {
    id: 3,
    dayLabel: 'Day 2 (Morning)',
    dateStr: '12 November 2026',
    timeRange: '07:00 AM – 11:30 AM',
    title: 'Registration, ID Badges & The Grand Inauguration',
    subtitle: 'Badged Up in Navy T-Shirts & Discovering the Pre-Build Twist',
    location: 'BPUT Grand Seminar Hall & Hackathon Arena',
    statusBadge: {
      text: 'HACKATHON KICKOFF',
      type: 'gold'
    },
    storyParagraphs: [
      'Alarms rang at 7:00 AM. Fueled by excitement, we quickly freshened up, had breakfast, and walked to the central registration desk. We verified our credentials and were handed our official BPUT Hackathon ID cards and event T-shirts.',
      'We attended the prestigious inaugural welcome ceremony in the Grand Seminar Hall. With 500+ participants, university dignitaries, and industry delegates from OKCL in attendance, the electric energy of the arena hit us.',
      'Then came the sudden twist: we discovered that most participating teams had arrived with substantial pre-built architectures! We only had preliminary prototypes and concepts we had demonstrated in Bhubaneswar. We had to build, test, and ship our full production cybersecurity system right here from scratch.'
    ],
    keyHighlight: {
      title: 'The Realization',
      description: 'Facing 500+ competitors with raw prototypes — our 36-hour sprint had officially begun.',
      badge: 'SEMINAR HALL',
      icon: 'Award'
    },
    photos: [
      {
        src: '/images/journey/ch3_morning_tshirts.jpg',
        caption: 'Morning 8:15 AM: Freshly badged up in official hackathon t-shirts',
        timestamp: '12 Nov 2026 • 08:15 AM',
        tag: 'Registration'
      },
      {
        src: '/images/journey/ch3_team_ready.jpg',
        caption: 'Team VEX LOG fully geared with identity badges ready for battle',
        timestamp: '12 Nov 2026 • 08:16 AM',
        tag: 'Team VEX LOG'
      },
      {
        src: '/images/journey/ch3_registration_desk_badge.jpg',
        caption: 'Collecting official credentials and orientation kit at registration',
        timestamp: '12 Nov 2026 • 09:46 AM',
        tag: 'Helpdesk'
      },
      {
        src: '/images/journey/ch3_hackathon_arena_setup.jpg',
        caption: 'Setting up equipment and cables at our assigned station desk',
        timestamp: '12 Nov 2026 • 10:33 AM',
        tag: 'Arena Setup'
      }
    ]
  },
  {
    id: 4,
    dayLabel: 'Day 2 (Afternoon)',
    dateStr: '12 November 2026',
    timeRange: '11:30 AM – 06:00 PM',
    title: 'The Wi-Fi Collapse & The Single-Laptop Crucible',
    subtitle: 'Git Sync Breakdown, Rapid Role Adaptation & First Evaluation Win',
    location: 'Team Station, Problem Statement 09 Arena',
    statusBadge: {
      text: 'CRITICAL CRISIS OVERCOME',
      type: 'red'
    },
    storyParagraphs: [
      'As hundreds of developers simultaneously booted their laptops, the venue Wi-Fi network stalled. Our git pushes timed out, pulls threw SSL errors, and distributed team synchronization collapsed into chaos.',
      'Seeing panic creeping in, we made a decisive, high-stakes choice: abandon multi-machine sync and collapse our entire development workflow onto A SINGLE LAPTOP! We clustered around one screen: one typing, two inspecting code, one verifying threat datasets, and one preparing presentation flows.',
      'Initially, several modules threw compilation errors. We took a deep breath, recalibrated, and worked methodically. After lunch, the BPUT organizers provided gift hampers, water bottles, hot tea, and evening soup that kept our spirits up. By late afternoon, we presented our 1st Evaluation to the judges — and aced it!'
    ],
    keyHighlight: {
      title: 'Single-Laptop Strategy',
      description: 'When git failed and Wi-Fi crashed, 5 developers coordinated seamlessly around a single keyboard.',
      badge: 'TACTICAL SHIFT',
      icon: 'Laptop'
    },
    photos: [
      {
        src: '/images/uploaded/team_workstation_desk.jpg',
        caption: 'Team VEX LOG stationed at our hackathon desk with laptops and official team standee',
        timestamp: '12 Nov 2026 • 12:45 PM',
        tag: 'Workstation'
      },
      {
        src: '/images/journey/ch4_single_laptop_crisis.jpg',
        caption: 'The single-laptop huddle: Collaborating intensely after network sync failed',
        timestamp: '12 Nov 2026 • 03:00 PM',
        tag: 'Single-Laptop Shift'
      },
      {
        src: '/images/journey/ch4_single_laptop_screen.jpg',
        caption: 'Intense code reviews huddled around the single working laptop',
        timestamp: '12 Nov 2026 • 03:00 PM',
        tag: 'Code Review'
      },
      {
        src: '/images/journey/ch4_afternoon_grind.jpg',
        caption: 'Delivering our 1st successful evaluation round before the jury panel',
        timestamp: '12 Nov 2026 • 03:01 PM',
        tag: 'Evaluation 01'
      }
    ]
  },
  {
    id: 5,
    dayLabel: 'Day 2 (Night) → Day 3',
    dateStr: '12–13 November 2026',
    timeRange: '08:00 PM – 04:00 AM',
    title: 'Campus Campfire & The Restless 4 AM All-Nighter',
    subtitle: 'Call/SMS Bombing Defense, CamPhishing, Localhost AI Nightmares',
    location: 'BPUT Campus Grounds & Overnight Hackathon Floor',
    statusBadge: {
      text: 'THE 4:00 AM CRUCIBLE',
      type: 'amber'
    },
    storyParagraphs: [
      'Following dinner, BPUT organized an open-air campfire in the campus courtyard. We stepped out into the chill for 20 minutes to absorb the fire’s warmth, chat with other teams, and clear our heads before the night sprint.',
      'Returning to our station, Ankit and I buckled down for an all-night hackathon marathon. We set out to implement real-time Call Blocking, SMS Bombing Blocker, and CamPhishing link detection. Then came our biggest technical miscalculation: attempting to bind cloud AI endpoints onto an Android app over spotty venue internet! Localhost ports refused to bridge, dashboards crashed, and latency spiked.',
      'Hour after grueling hour, we rewrote the stack to favor native, on-device heuristics. At 4:00 AM, the core systems finally stabilized and green test indicators lit up! Padmalochan woke up right on time to take over the monitoring shift, I collapsed onto a sleeping mat, while Ankit pushed forward until 7:00 AM.'
    ],
    keyHighlight: {
      title: 'The 4:00 AM Handover',
      description: 'Stabilizing native Android heuristics at 4:00 AM before handing the shift to Padmalochan.',
      badge: 'ALL-NIGHTER',
      icon: 'Flame'
    },
    photos: [
      {
        src: '/images/uploaded/campus_campfire_crowd.jpg',
        caption: 'Students, participants, and mentors gathered around the courtyard campfire at night',
        timestamp: '12 Nov 2026 • 08:35 PM',
        tag: 'Campfire Gathering'
      },
      {
        src: '/images/uploaded/campfire_bonfire_high.jpg',
        caption: 'The tall wooden bonfire blazing high into the chill night sky',
        timestamp: '12 Nov 2026 • 08:40 PM',
        tag: 'Bonfire Flames'
      },
      {
        src: '/images/journey/ch5_midnight_hacking.jpg',
        caption: '11:21 PM: Locked into intensive debugging of call and SMS blocking engines',
        timestamp: '12 Nov 2026 • 11:21 PM',
        tag: 'Night Sprint'
      },
      {
        src: '/images/journey/ch5_late_night_focus.jpg',
        caption: 'Writing native Android background services and IPC wrappers at 11:36 PM',
        timestamp: '12 Nov 2026 • 11:36 PM',
        tag: '4 AM Shift'
      }
    ]
  },
  {
    id: 6,
    dayLabel: 'Day 3 (Daytime)',
    dateStr: '13 November 2026',
    timeRange: '07:00 AM – 04:30 PM',
    title: '2nd Evaluation Glory, Student Visitors & The Panic Build',
    subtitle: 'Flawless Bad USB Demo, Booth Crowds & 5–6 Frantic Rebuilds',
    location: 'Hackathon Booth & Evaluation Room Hallway',
    statusBadge: {
      text: 'HIGH PRESSURE TRIALS',
      type: 'blue'
    },
    storyParagraphs: [
      'I woke up at 7:00 AM and promptly ordered Ankit to get some sleep after his heroic all-night shift. Soon after, the 2nd Evaluation began. Our Bad USB detection, CamPhishing blocker, and AI image inspection ran flawlessly. The jury was visibly impressed.',
      'Throughout the morning and afternoon, visiting school students, college delegations, and mentors stopped by our booth. We proudly demonstrated how VEX protects users against deceptive phishing and hardware attacks.',
      'Then disaster struck. A minor code change introduced minutes before the final round silently cascaded and broke the entire build! Frantically, we reverted, re-coded, and rebuilt the APK 5 to 6 times under sheer adrenaline, barely stabilizing the release binary as our final call approached.'
    ],
    keyHighlight: {
      title: '5–6 Frantic Rebuilds',
      description: 'Fixing a fatal build crash with minutes left on the clock before the final jury summon.',
      badge: 'RACE AGAINST TIME',
      icon: 'AlertTriangle'
    },
    photos: [
      {
        src: '/images/journey/ch6_booth_demo_prep.jpg',
        caption: 'Preparing live threat blocking demonstrations for visiting student delegations',
        timestamp: '13 Nov 2026 • 11:00 AM',
        tag: 'Booth Demos'
      },
      {
        src: '/images/journey/ch6_afternoon_rebuild_testing.jpg',
        caption: 'Frantic debugging: Re-testing and re-building the APK 5 to 6 times after a fatal break',
        timestamp: '13 Nov 2026 • 01:30 PM',
        tag: 'Build Panic'
      },
      {
        src: '/images/journey/ch6_final_eval_wait.jpg',
        caption: 'Tense moments at 2:42 PM: Standing by after 2nd evaluation as final room looms',
        timestamp: '13 Nov 2026 • 02:42 PM',
        tag: 'Final Evaluation Call'
      }
    ]
  },
  {
    id: 7,
    dayLabel: 'Day 3 (Climax)',
    dateStr: '13 November 2026',
    timeRange: '04:30 PM – 05:45 PM',
    title: 'The Zero-Internet Final Evaluation & Android Offline Miracle',
    subtitle: 'When Everything Failed, Offline Caching Saved Team VEX LOG',
    location: 'Closed Evaluation Room, BPUT Academic Block',
    statusBadge: {
      text: 'OFFLINE RESILIENCE MIRACLE',
      type: 'gold'
    },
    storyParagraphs: [
      'Our final evaluation was scheduled in a separate, isolated room. When we entered and set up, our hearts sank: there was zero Wi-Fi connectivity. I tried hotspotting from a friend’s phone, but mobile data flatlined inside the thick walls. Our web dashboard crashed and live Linux testing scripts froze.',
      'We had exactly 10 minutes to prove our worth. And then, our engineering design paid off in the most unexpected way: because we had architected VEX natively on Android with local caching and in-memory background processing, the Android application ran independently without needing an active internet connection!',
      'Bad USB detection fired instantly upon insertion. AI Image and Video anomaly classifiers ran on cached pipelines. Bad URL defense and the VEX Secure Browser functioned without lag, and Ankit’s Call & SMS Bombing Blocker intercepted threats in real time. We left the room thinking our presentation had been too messy to win, completely drained and emotionally shattered.'
    ],
    keyHighlight: {
      title: 'Why Offline Android Saved Us',
      description: 'When Linux scripts and cloud dashboards failed, native Android caching kept VEX 100% operational.',
      badge: 'ENGINEERING TRIUMPH',
      icon: 'ShieldCheck'
    },
    photos: [
      {
        src: '/images/journey/ch7_trophy_certificate.jpg',
        caption: 'The hard-earned 3rd Prize Bronze Trophy for Problem Statement 09',
        timestamp: '13 Nov 2026 • 05:15 PM',
        tag: 'Podium Trophy'
      },
      {
        src: '/images/journey/ch7_award_certificate_wa.jpg',
        caption: 'Official 3rd Prize Merit Certificate awarded by BPUT and OKCL',
        timestamp: '13 Nov 2026 • 05:15 PM',
        tag: 'Merit Certificate'
      },
      {
        src: '/images/uploaded/auditorium_stage_decorated.jpg',
        caption: 'The Grand Seminar Hall stage decorated for the valedictory award distribution',
        timestamp: '13 Nov 2026 • 04:30 PM',
        tag: 'Award Stage'
      }
    ]
  },
  {
    id: 8,
    dayLabel: 'Day 3 (Triumph)',
    dateStr: '13 November 2026',
    timeRange: '05:45 PM – 08:30 PM',
    title: 'The Disqualification Panic, Lost Room Key & The 3rd Prize Call!',
    subtitle: 'From Despair and Lost Keys to Standing on Stage with Our Mentor',
    location: 'Mahanadi Hostel ➔ BPUT Grand Seminar Hall Stage',
    statusBadge: {
      text: 'STAGE VICTORY & PODIUM FINISH',
      type: 'gold'
    },
    storyParagraphs: [
      'As we packed our bags in silent grief believing we had failed, Ankit suddenly received a call from an event volunteer: our team was on the verge of being DISQUALIFIED due to a clerical discrepancy in team names! We explained desperately, but had lost all hope. Walking back to Mahanadi Hostel, another misfortune struck: we had lost our hostel room key! After a frantic search and resolving the room access, we dragged ourselves back to the Grand Seminar Hall with zero expectations.',
      'As the award announcements for Problem Statement 09 CYBERGUARD began, we held our breath, whispering a silent prayer: "Please God, just mention our name once..."',
      'And then the speakers boomed: "And the 3rd Prize goes to... TEAM VEX LOG!" An explosion of pure ecstasy, relief, and roaring cheers erupted from our corner! We rushed to the stage alongside our mentor, Mr. Kodanda Dhar Naik (Assistant Professor, CSE, PMEC). Hoisting the heavy trophy, receiving the prize and certificate, and celebrating under the stage lights was the greatest moment of our lives!'
    ],
    keyHighlight: {
      title: 'Tears to Triumph',
      description: 'Overcoming the disqualification scare and lost room keys to hoist 3rd Prize with Mentor Mr. K.D. Naik.',
      badge: '3RD PRIZE CHAMPIONS',
      icon: 'Award'
    },
    photos: [
      {
        src: '/images/uploaded/stage_led_victory.jpg',
        caption: 'Crowning Grand Finale Winner Moment: LED screen announcing VEX LOG from PMEC Berhampur receiving 3rd Prize and ₹10,000 Cheque',
        timestamp: '13 Nov 2026 • 04:50 PM',
        tag: 'Grand Finale Winner'
      },
      {
        src: '/images/journey/ch8_trophy_presentation_closeup.jpg',
        caption: 'Receiving the heavy bronze trophy on stage with Mentor Mr. Kodanda Dhar Naik',
        timestamp: '13 Nov 2026 • 04:53 PM',
        tag: 'With Mentor'
      },
      {
        src: '/images/journey/ch7_team_celebration.jpg',
        caption: 'Roaring celebration with the trophy after surviving impossible odds',
        timestamp: '13 Nov 2026 • 05:05 PM',
        tag: 'Stage Ecstasy'
      },
      {
        src: '/images/uploaded/team_certificates_all5.jpg',
        caption: 'Team VEX LOG: All 5 student engineers holding our official BPUT Hackathon Merit Certificates',
        timestamp: '13 Nov 2026 • 05:10 PM',
        tag: 'All 5 Champions'
      }
    ]
  },
  {
    id: 9,
    dayLabel: 'Day 4 & 5 (Detour & Return)',
    dateStr: '14–15 November 2026',
    timeRange: '06:00 AM – Next Morning',
    title: 'Sambalpur Victory Detour & The Sacred Temples of Western Odisha',
    subtitle: 'Huma Leaning Temple, Maa Ghanteswari, Maa Samaleswari & Night Bus Home',
    location: 'Sambalpur Junction ➔ Huma ➔ Ghanteswari ➔ Samaleswari ➔ Berhampur',
    statusBadge: {
      text: 'PILGRIMAGE OF GRATITUDE',
      type: 'emerald'
    },
    storyParagraphs: [
      'The morning after our victory, we boarded the train to return home, but learned the train was indefinitely delayed. Instead of sulking on a platform, we turned it into a spontaneous victory celebration: we disembarked at Sambalpur Junction at the crack of dawn!',
      'We dropped our luggage at a relative’s house, freshened up, and hired a car for a memorable tour of Sambalpur’s iconic heritage. We visited Huma Dhuma — the world-famous Leaning Temple of Lord Shiva situated on the scenic bank of the Mahanadi River. We then journeyed to the sacred Maa Ghanteswari Temple, mesmerized by the ringing harmony of tens of thousands of brass bells.',
      'In the afternoon, we sought divine blessings at the historic Maa Samaleswari Temple, the revered guardian deity of Western Odisha. Finally, we arrived at Laxmitalkies bus stand, relished a delicious family dinner at an uncle’s home, and boarded the overnight bus back to Berhampur — returning to PMEC as proud 3rd Prize Hackathon Champions!'
    ],
    keyHighlight: {
      title: 'Sacred Temples of Western Odisha',
      description: 'Offering prayers of gratitude at Huma Leaning Temple, Maa Ghanteswari & Maa Samaleswari.',
      badge: 'VICTORY ROADTRIP',
      icon: 'Compass'
    },
    photos: [
      {
        src: '/images/journey/ch8_sambalpur_morning.jpg',
        caption: 'Dawn arrival at Sambalpur Junction at 6:34 AM after train delays',
        timestamp: '14 Nov 2026 • 06:34 AM',
        tag: 'Sambalpur'
      },
      {
        src: '/images/uploaded/team_outdoor_selfie.jpg',
        caption: 'Brothers in code: Smiling morning roadtrip selfie across Sambalpur',
        timestamp: '14 Nov 2026 • 07:15 AM',
        tag: 'Victory Roadtrip'
      },
      {
        src: '/images/journey/ch8_huma_temple.jpg',
        caption: 'Visiting Huma Dhuma — The world-famous Leaning Temple of Lord Shiva',
        timestamp: '14 Nov 2026 • 07:58 AM',
        tag: 'Huma Temple'
      },
      {
        src: '/images/journey/ch8_huma_mahanadi_river.jpg',
        caption: 'The scenic banks of the sacred Mahanadi River next to Huma temple',
        timestamp: '14 Nov 2026 • 08:00 AM',
        tag: 'Mahanadi River'
      },
      {
        src: '/images/journey/ch8_ghanteswari_bells.jpg',
        caption: 'Maa Ghanteswari Temple surrounded by thousands of resonant brass bells',
        timestamp: '14 Nov 2026 • 08:21 AM',
        tag: 'Ghanteswari'
      },
      {
        src: '/images/journey/ch9_samaleswari_temple.jpg',
        caption: 'Seeking divine blessings at the revered Maa Samaleswari Temple',
        timestamp: '14 Nov 2026 • 04:14 PM',
        tag: 'Samaleswari'
      },
      {
        src: '/images/journey/ch9_uncle_dinner_sambalpur.jpg',
        caption: 'Warm family dinner and hospitality at uncle\'s home in Sambalpur',
        timestamp: '14 Nov 2026 • 07:45 PM',
        tag: 'Family Dinner'
      },
      {
        src: '/images/journey/ch9_bus_stand_night.jpg',
        caption: 'Boarding the overnight bus at Laxmitalkies bus stand back to Berhampur',
        timestamp: '14 Nov 2026 • 09:20 PM',
        tag: 'Homebound'
      }
    ]
  }
];

export const HackathonJourney: React.FC = () => {
  const [selectedDayFilter, setSelectedDayFilter] = useState<string>('all');
  const [activePhoto, setActivePhoto] = useState<PhotoItem | null>(null);

  // Flatten all photos for continuous lightbox carousel
  const allPhotos = React.useMemo(() => {
    return JOURNEY_CHAPTERS.flatMap(ch => ch.photos);
  }, []);

  const activePhotoIndex = activePhoto 
    ? allPhotos.findIndex(p => p.src === activePhoto.src)
    : -1;

  const handlePrevPhoto = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (activePhotoIndex > 0) {
      sounds.playClick(650, 0.03);
      setActivePhoto(allPhotos[activePhotoIndex - 1]);
    } else {
      sounds.playClick(650, 0.03);
      setActivePhoto(allPhotos[allPhotos.length - 1]);
    }
  };

  const handleNextPhoto = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (activePhotoIndex < allPhotos.length - 1) {
      sounds.playClick(750, 0.03);
      setActivePhoto(allPhotos[activePhotoIndex + 1]);
    } else {
      sounds.playClick(750, 0.03);
      setActivePhoto(allPhotos[0]);
    }
  };

  const filterOptions = [
    { id: 'all', label: 'All Chapters (1–9)' },
    { id: 'Day 1', label: '11 Nov (The Train)' },
    { id: 'Day 2', label: '12 Nov (The 36h Sprint)' },
    { id: 'Day 3', label: '13 Nov (Victory & Mentors)' },
    { id: 'Day 4', label: '14 Nov (Sambalpur Detour)' },
  ];

  const filteredChapters = JOURNEY_CHAPTERS.filter((ch) => {
    if (selectedDayFilter === 'all') return true;
    if (selectedDayFilter === 'Day 1') return ch.dayLabel.includes('Day 1');
    if (selectedDayFilter === 'Day 2') return ch.dayLabel.includes('Day 2');
    if (selectedDayFilter === 'Day 3') return ch.dayLabel.includes('Day 3');
    if (selectedDayFilter === 'Day 4') return ch.dayLabel.includes('Day 4');
    return true;
  });

  const handleOpenPhoto = (photo: PhotoItem) => {
    sounds.playClick(750, 0.04);
    setActivePhoto(photo);
  };

  const handleClosePhoto = () => {
    sounds.playClick(500, 0.03);
    setActivePhoto(null);
  };

  return (
    <section id="hackathon-journey" className="py-24 bg-[#070A11] border-b-2 border-[#1E293B] relative overflow-hidden">
      {/* Background Cyber Texture */}
      <div className="absolute inset-0 bg-cyber-grid opacity-20 pointer-events-none" />
      <div className="absolute -top-32 right-1/4 w-96 h-96 bg-blue-600/10 rounded-full blur-[140px] pointer-events-none" />
      <div className="absolute bottom-10 left-10 w-96 h-96 bg-amber-500/10 rounded-full blur-[140px] pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-14 space-y-4">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-xl bg-amber-500/15 border-2 border-amber-500 text-amber-400 font-mono text-xs font-bold uppercase tracking-wider shadow-solid-gold">
            <Camera className="w-4 h-4 text-amber-400" />
            <span>AUTHENTIC TIMELINE & PHOTO STORY</span>
          </div>
          
          <h2 className="text-3xl sm:text-5xl font-heading font-black text-white tracking-tight">
            The Saga of <span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 via-yellow-200 to-amber-500">Team VEX LOG</span>
          </h2>
          
          <p className="text-base sm:text-lg text-slate-300 font-normal leading-relaxed">
            The unfiltered, chronological chronicle of five students from PMEC Berhampur: from a delayed MEMO train 
            and a single-laptop crisis, to the zero-internet final room showdown, an eleventh-hour disqualification panic, 
            and hoisting the 3rd Prize trophy with our faculty mentors.
          </p>
        </div>

        {/* Tactical Stats Banner */}
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 mb-12">
          <div className="bg-[#0B1120] border-2 border-[#1E293B] p-3.5 rounded-xl text-center shadow-solid">
            <div className="text-2xl font-black font-mono text-amber-400">120+ hrs</div>
            <div className="text-[11px] font-mono text-slate-400 uppercase tracking-wider mt-1">Total Odyssey</div>
          </div>
          <div className="bg-[#0B1120] border-2 border-[#1E293B] p-3.5 rounded-xl text-center shadow-solid">
            <div className="text-2xl font-black font-mono text-blue-400">1 Laptop</div>
            <div className="text-[11px] font-mono text-slate-400 uppercase tracking-wider mt-1">Single-Workstation Shift</div>
          </div>
          <div className="bg-[#0B1120] border-2 border-[#1E293B] p-3.5 rounded-xl text-center shadow-solid">
            <div className="text-2xl font-black font-mono text-red-400">0 KB/s</div>
            <div className="text-[11px] font-mono text-slate-400 uppercase tracking-wider mt-1">Internet in Final Room</div>
          </div>
          <div className="bg-[#0B1120] border-2 border-[#1E293B] p-3.5 rounded-xl text-center shadow-solid">
            <div className="text-2xl font-black font-mono text-emerald-400">100%</div>
            <div className="text-[11px] font-mono text-slate-400 uppercase tracking-wider mt-1">Android Offline Cache</div>
          </div>
          <div className="bg-[#0B1120] border-2 border-amber-500 p-3.5 rounded-xl text-center shadow-solid-gold bg-amber-500/10">
            <div className="text-2xl font-black font-mono text-amber-300">3rd Prize</div>
            <div className="text-[11px] font-mono text-amber-200 uppercase tracking-wider mt-1">State Podium Finish</div>
          </div>
          <div className="bg-[#0B1120] border-2 border-[#1E293B] p-3.5 rounded-xl text-center shadow-solid">
            <div className="text-2xl font-black font-mono text-purple-400">3 Shrines</div>
            <div className="text-[11px] font-mono text-slate-400 uppercase tracking-wider mt-1">Sambalpur Temples</div>
          </div>
        </div>

        {/* Day Filter Pills */}
        <div className="flex flex-wrap items-center justify-center gap-2 mb-12">
          {filterOptions.map((opt) => (
            <button
              key={opt.id}
              onClick={() => {
                sounds.playClick(650, 0.03);
                setSelectedDayFilter(opt.id);
              }}
              className={`px-4 py-2 rounded-xl font-mono text-xs font-bold transition-all border-2 ${
                selectedDayFilter === opt.id
                  ? 'bg-blue-600 border-blue-400 text-white shadow-solid-blue'
                  : 'bg-[#0F172A] border-[#1E293B] text-slate-300 hover:border-slate-500 hover:text-white'
              }`}
            >
              {opt.label}
            </button>
          ))}
        </div>

        {/* Timeline Chapters List */}
        <div className="space-y-12">
          {filteredChapters.map((chapter) => (
            <div 
              key={chapter.id}
              className="bg-[#0B101D] border-2 border-[#1E293B] rounded-2xl p-6 sm:p-8 shadow-solid relative overflow-hidden"
            >
              {/* Top Meta Bar */}
              <div className="flex flex-wrap items-center justify-between gap-4 pb-6 border-b border-[#1E293B]">
                <div className="flex flex-wrap items-center gap-3">
                  <div className="px-3 py-1 rounded-lg bg-blue-600/20 border border-blue-500 text-blue-400 font-mono text-xs font-bold">
                    Chapter {chapter.id.toString().padStart(2, '0')} // {chapter.dayLabel}
                  </div>
                  
                  <div className="flex items-center gap-1.5 text-slate-400 font-mono text-xs">
                    <Calendar className="w-3.5 h-3.5 text-slate-500" />
                    <span>{chapter.dateStr}</span>
                  </div>

                  <div className="flex items-center gap-1.5 text-slate-400 font-mono text-xs">
                    <Clock className="w-3.5 h-3.5 text-slate-500" />
                    <span>{chapter.timeRange}</span>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <div className="flex items-center gap-1 text-slate-400 text-xs font-mono">
                    <MapPin className="w-3.5 h-3.5 text-amber-400" />
                    <span>{chapter.location}</span>
                  </div>
                </div>
              </div>

              {/* Main Content Grid: Story text & Highlight */}
              <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 my-6 items-start">
                
                {/* Story Prose */}
                <div className="lg:col-span-8 space-y-4">
                  <div>
                    <h3 className="text-2xl sm:text-3xl font-heading font-black text-white tracking-tight">
                      {chapter.title}
                    </h3>
                    <p className="text-sm font-mono text-blue-400 font-semibold mt-1">
                      {chapter.subtitle}
                    </p>
                  </div>

                  <div className="space-y-3.5 text-slate-300 text-sm sm:text-base leading-relaxed font-normal">
                    {chapter.storyParagraphs.map((paragraph, pIdx) => (
                      <p key={pIdx}>{paragraph}</p>
                    ))}
                  </div>
                </div>

                {/* Tactical Highlight Card */}
                {chapter.keyHighlight && (
                  <div className="lg:col-span-4 bg-[#0F172A] border-2 border-[#1E293B] rounded-xl p-5 space-y-3 shadow-inner">
                    <div className="flex items-center justify-between">
                      <span className="text-[10px] font-mono font-bold px-2 py-0.5 rounded bg-blue-600/20 border border-blue-500/40 text-blue-300 uppercase tracking-wider">
                        {chapter.keyHighlight.badge}
                      </span>
                      <Sparkles className="w-4 h-4 text-amber-400" />
                    </div>

                    <h4 className="font-heading font-bold text-white text-base">
                      {chapter.keyHighlight.title}
                    </h4>

                    <p className="text-xs text-slate-300 leading-relaxed font-normal">
                      {chapter.keyHighlight.description}
                    </p>

                    <div className="pt-2 border-t border-[#1E293B] flex items-center gap-2 text-[11px] font-mono text-emerald-400 font-semibold">
                      <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                      <span>Verified Event from Field Logs</span>
                    </div>
                  </div>
                )}
              </div>

              {/* Photo Strip / Gallery */}
              <div className="mt-6 pt-6 border-t border-[#1E293B]">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-2 text-xs font-mono text-slate-400 uppercase tracking-wider">
                    <Camera className="w-4 h-4 text-amber-400" />
                    <span>Captured Moments ({chapter.photos.length} Photos)</span>
                  </div>
                  <span className="text-[11px] font-mono text-slate-500">
                    Click any photo to zoom & read field log
                  </span>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
                  {chapter.photos.map((photo, pIdx) => (
                    <div 
                      key={pIdx}
                      onClick={() => handleOpenPhoto(photo)}
                      className="group cursor-pointer bg-[#0F172A] border-2 border-[#1E293B] hover:border-blue-500 rounded-xl overflow-hidden transition-all transform hover:-translate-y-1 shadow-solid hover:shadow-solid-blue flex flex-col"
                    >
                      {/* Image Thumbnail */}
                      <div className="relative aspect-[4/3] bg-black overflow-hidden">
                        <img 
                          src={asset(photo.src)} 
                          alt={photo.caption}
                          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                          loading="lazy"
                        />
                        <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-60 group-hover:opacity-40 transition-opacity" />
                        
                        <div className="absolute top-2 right-2 p-1.5 rounded-lg bg-black/60 backdrop-blur-md text-white opacity-0 group-hover:opacity-100 transition-opacity">
                          <Maximize2 className="w-3.5 h-3.5" />
                        </div>

                        <div className="absolute bottom-2 left-2">
                          <span className="px-2 py-0.5 rounded bg-blue-600/90 text-white font-mono text-[9px] font-bold uppercase tracking-wider">
                            {photo.tag}
                          </span>
                        </div>
                      </div>

                      {/* Photo Caption */}
                      <div className="p-3 flex-1 flex flex-col justify-between space-y-1.5 bg-[#0B101D]">
                        <p className="text-xs font-medium text-slate-200 line-clamp-2 leading-snug group-hover:text-blue-300 transition-colors">
                          {photo.caption}
                        </p>
                        <p className="text-[10px] font-mono text-slate-500 flex items-center gap-1">
                          <Clock className="w-3 h-3 text-slate-600" />
                          <span>{photo.timestamp}</span>
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

            </div>
          ))}
        </div>

        {/* Post-Hackathon Gratitude Box */}
        <div className="mt-16 bg-gradient-to-r from-blue-950/40 via-[#0F172A] to-amber-950/30 border-2 border-[#1E293B] rounded-2xl p-8 text-center space-y-4 shadow-solid">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/40 text-amber-400 font-mono text-xs font-bold">
            <Heart className="w-3.5 h-3.5 text-amber-400 fill-amber-400" />
            <span>HEARTFELT ACKNOWLEDGMENTS</span>
          </div>

          <h3 className="text-2xl sm:text-3xl font-heading font-black text-white">
            Gratitude to Our Mentors, BPUT, and Parala Maharaja Engineering College
          </h3>

          <p className="text-sm sm:text-base text-slate-300 max-w-3xl mx-auto leading-relaxed">
            This victory would not have been possible without the constant encouragement and technical guidance of 
            <strong className="text-white"> Mr. Kodanda Dhar Naik (K.D. Naik, Assistant Professor, CSE)</strong> and 
            <strong className="text-white"> Mr. Kamalakanta Rout (Faculty Member, CSE)</strong>, along with the 
            visionary faculty and leadership of <a href="https://pmec.ac.in/" target="_blank" rel="noopener noreferrer" className="text-blue-400 underline font-semibold">PMEC Berhampur</a>. 
            We also extend our immense thanks to the BPUT Hackathon organizers and OKCL for hosting an intense, world-class arena.
          </p>

          <div className="pt-4 flex flex-wrap items-center justify-center gap-4">
            <a
              href="#team-section"
              onClick={() => sounds.playClick(700, 0.04)}
              className="px-5 py-2.5 rounded-xl bg-blue-600 text-white font-mono text-xs font-bold uppercase tracking-wider hover:bg-blue-500 transition-colors shadow-solid-blue"
            >
              Meet the Team & Mentors
            </a>
            <a
              href="https://github.com/cbagh0819-ctrl/VEX"
              target="_blank"
              rel="noopener noreferrer"
              onClick={() => sounds.playClick(700, 0.04)}
              className="px-5 py-2.5 rounded-xl bg-[#0F172A] border-2 border-[#1E293B] text-slate-200 font-mono text-xs font-bold uppercase tracking-wider hover:border-slate-500 hover:text-white transition-colors"
            >
              Inspect Source on GitHub
            </a>
          </div>
        </div>

      </div>

      {/* Lightbox Photo Zoom Modal */}
      {activePhoto && (
        <div 
          className="fixed inset-0 z-50 bg-black/90 backdrop-blur-md flex items-center justify-center p-4 sm:p-6"
          onClick={handleClosePhoto}
        >
          <div 
            className="relative max-w-4xl w-full bg-[#0B101D] border-2 border-slate-700 rounded-2xl overflow-hidden shadow-2xl"
            onClick={(e) => e.stopPropagation()}
          >
            {/* Modal Header */}
            <div className="flex items-center justify-between p-4 bg-[#0F172A] border-b border-[#1E293B]">
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-1 rounded-md bg-blue-600 text-white font-mono text-xs font-bold uppercase tracking-wider">
                  {activePhoto.tag}
                </span>
                <span className="text-xs font-mono text-slate-400">
                  {activePhoto.timestamp}
                </span>
                <span className="text-xs font-mono text-slate-500 ml-2">
                  ({activePhotoIndex + 1} of {allPhotos.length})
                </span>
              </div>

              <div className="flex items-center gap-2">
                <button 
                  onClick={handlePrevPhoto}
                  className="p-1.5 rounded-lg bg-[#1E293B] hover:bg-blue-600 text-slate-300 hover:text-white transition-colors"
                  title="Previous Photo"
                >
                  <ChevronLeft className="w-5 h-5" />
                </button>
                <button 
                  onClick={handleNextPhoto}
                  className="p-1.5 rounded-lg bg-[#1E293B] hover:bg-blue-600 text-slate-300 hover:text-white transition-colors"
                  title="Next Photo"
                >
                  <ChevronRight className="w-5 h-5" />
                </button>
                <button 
                  onClick={handleClosePhoto}
                  className="p-1.5 rounded-lg bg-[#1E293B] hover:bg-red-600 text-slate-300 hover:text-white transition-colors ml-2"
                  title="Close (Esc)"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* Modal Image Display */}
            <div className="relative max-h-[70vh] bg-black flex items-center justify-center overflow-hidden p-2">
              <img 
                src={asset(activePhoto.src)} 
                alt={activePhoto.caption}
                className="max-h-[68vh] w-auto max-w-full object-contain mx-auto rounded-lg shadow-lg"
              />

              {/* Overlay Navigation Arrows */}
              <button
                onClick={handlePrevPhoto}
                className="absolute left-3 top-1/2 -translate-y-1/2 p-2 rounded-full bg-black/60 hover:bg-blue-600 text-white backdrop-blur-sm transition-all"
                title="Previous Photo"
              >
                <ChevronLeft className="w-6 h-6" />
              </button>
              <button
                onClick={handleNextPhoto}
                className="absolute right-3 top-1/2 -translate-y-1/2 p-2 rounded-full bg-black/60 hover:bg-blue-600 text-white backdrop-blur-sm transition-all"
                title="Next Photo"
              >
                <ChevronRight className="w-6 h-6" />
              </button>
            </div>

            {/* Modal Caption Footer */}
            <div className="p-4 bg-[#0B101D] border-t border-[#1E293B] space-y-1">
              <p className="text-sm sm:text-base font-semibold text-white">
                {activePhoto.caption}
              </p>
              <p className="text-xs font-mono text-slate-400">
                Official Hackathon Journey Field Documentation • Team VEX LOG
              </p>
            </div>
          </div>
        </div>
      )}

    </section>
  );
};
