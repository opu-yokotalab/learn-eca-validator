% Ÿ‚Ì‹³Şƒ‚ƒWƒ…[ƒ‹‚ğ—v‹
next("start","id1",_, []).
next("id1","id2",LI, []):-not moduleMember(LI,"X"),not moduleMember(LI,"Y").
next("id1","id2",LI, []):-not moduleMember(LI,"X"),moduleMember(LI,"Y").
next("id1","id2",LI, []):-moduleMember(LI,"X"),not moduleMember(LI,"Y").
next("id2","id3",_, []).
next("id3","id1",_, []).
next("id1","id4",LI, []):-moduleMember(LI,"X"),moduleMember(LI,"Y").
next("id4","id5",LI, []):-moduleMember(LI,"X").
next("id4","id6",LI, []):-not moduleMember(LI,"X").
next("id5","end",_, []).
next("id6","id5",_, []).