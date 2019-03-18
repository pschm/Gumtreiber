package de.psst.gumtreiber.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Enum representation of the rooms.
 * Each room has its geographical coordinates assigned.
 */
public enum Room {

    //Hauptgebäude Ost-Trakt x.1xx
    R3100(51.023025, 7.562189, 3,"Seminar 6"),
    R3101(51.023000, 7.562344, 3, "Seminarsaal IV"),
    R3102(51.023117, 7.562346, 3, "euro engineering  Saal"),
    R3103(51.023227, 7.562328, 3, "Seminar 5"),
    R3104(51.023293, 7.562339, 3, "Seminar 4"),
    R3105(51.023323, 7.562305, 3, "UV/AV"),
    R3106(51.023423, 7.562317, 3, "FISIA BABCOCK Saal"),
    R3107(51.023520, 7.562307, 3, "Unitechnik Saal"),
    R3108(51.023597, 7.562282, 3, "Seminar 1"),
    R3109(51.023694, 7.562278, 3, "Seminarraum"),
    R3110(51.023639, 7.562138, 3, "Seminarraum"),
    R3111(51.023579, 7.562137, 3, "Seminarraum"),
    R3112(51.023483, 7.562122, 3, "Seminarraum"),
    R3113(51.023391, 7.562150, 3, "Mathe-PC-Pool"),
    R3114(51.023360, 7.562156, 3, "Mathe-Mitarb."),
    R3115(51.023335, 7.562190, 3, "Beh-WC"),
    R3116(51.023330, 7.562137, 3, "EDV-Raum"),
    R3117(51.023319, 7.562193, 3, "D-WC"),
    R3119(51.023281, 7.562166, 3, "H-WC"),

    R2100(51.023015, 7.562175, 2, "Seminar 7"),
    R2101(51.022975, 7.562340, 2, "Übungsraum"),
    R2102(51.023020, 7.562354, 2, "Übungsraum"),
    R2103(51.023039, 7.562359, 2, "Übungsraum"),
    R2104(51.023099, 7.562312, 2, "Übungsraum"),
    R2105(51.023165, 7.562347, 2, "SLZ"),
    R2106(51.023261, 7.562320, 2, "WI-PC-Pool I"),
    R2107(51.023444, 7.562314, 2, "WI-PC-Pool II"),
    R2108(51.023587, 7.562308, 2, "Medienraum"),
    R2109(51.023714, 7.562263, 2, "ADV-Terminalraum II"),
    R2110(51.023636, 7.562121, 2, "ADV-Terminalraum I"),
    R2111(51.023580, 7.562103, 2, "Supervisor"),
    R2112(51.023517, 7.562129, 2, "PC-Pool Mathe Ing."),
    R2113(51.023456, 7.562151, 2, "Seminarraum"),
    R2114(51.023372, 7.562142, 2, "Seminarraum"),
    R2115(51.023326, 7.562188, 2, "UV/AV"),
    R2116(51.023318, 7.562193, 2, "D-WC"),
    R2118(51.023288, 7.562157, 2, "H-WC"),

    R1101(51.023028, 7.562180, 1, "BAFÖG-Amt"),
    R1102(51.022968, 7.562349, 1, "Haushalt Beschaffung"),
    R1103(51.023026, 7.562365, 1, "Verwaltung Leiter"),
    R1104(51.023065, 7.562368, 1, "Verwaltung Azubi"),
    R1105(51.023102, 7.562339, 1, "Medienwart"),
    R1106(51.023129, 7.562309, 1, "Hausmeister"),
    R1107(51.023198, 7.562289, 1, "Pförtner / Loge"),
    R1108(51.023154, 7.562317, 1, "Post"),
    R1109(51.023240, 7.562331, 1, "Kopierstelle"),
    R1110(51.023289, 7.562333, 1, "Campussprecher"),
    R1111(51.023389, 7.562342, 1, "Kopierraum"),
    R1112(51.023427, 7.562325, 1, "Lehrbeauftragte"),
    R1113(51.023457, 7.562311, 1, "Prodekan"),
    R1114(51.023480, 7.562320, 1, "Prodekan"),
    R1115(51.023511, 7.562317, 1, "Hilfskraft"),
    R1116(51.023527, 7.562308, 1, "Dekans-Assistent"),
    R1117(51.023564, 7.562289, 1, "Dekan"),
    R1118(51.023611, 7.562284, 1, "Fakultätssekretariat ET"),
    R1119(51.023650, 7.562298, 1, "Fakultätssekretariat MT"),
    R1120(51.023710, 7.562300, 1, "Fakultätssekretariat INF"),
    R1121(51.023711, 7.562253, 1, "Teeküche"),
    R1122(51.023639, 7.562120, 1, "Kienbaum Saal"),
    R1123(51.023556, 7.562153, 1, "Fachschaft"),
    R1124(51.023523, 7.562169, 1, "Kopierer"),
    R1125(51.023493, 7.562133, 1, "Transferstelle"),
    R1126(51.023449, 7.562153, 1, "Prüfungsamt"),
    R1127(51.023386, 7.562147, 1, "Studierendensekretariat"),
    R1128(51.023354, 7.562175, 1, "Beh-WC"),
    R1129(51.023332, 7.562161, 1, "EDV Raum"),
    R1130(51.023356, 7.562130, 1, "UV/AV"),
    R1131(51.023304, 7.562167, 1, "D-WC"),
    R1135(51.023275, 7.562164, 1, "H-WC"),

    R0100(51.023093, 7.562336, 0, "ZBV"),
    R0101(51.023145, 7.562321, 0, "Büro Heizer"),
    R0102(51.023181, 7.562320, 0, "Elektrikwerkstatt"),
    R0103(51.023215, 7.562305, 0, "Sozialraum"),
    R0104(51.023259, 7.562302, 0, "Holzbearbeitung"),
    R0105(51.023284, 7.562317, 0, "Ausbildungswerkst ET"),
    R0106(51.023322, 7.562288, 0, "Putzmittel"),
    R0107(51.023348, 7.562284, 0, "CAD"),
    R0108(51.023373, 7.562294, 0, "Sachgebietsleiter"),
    R0109(51.023403, 7.562294, 0, "Ausbildungswerkst Met."),
    R0110(51.023433, 7.562299, 0, "Werkstattleiter"),
    R0111(51.023483, 7.562309, 0, "Blecherei"),
    R0112(51.023528, 7.562277, 0, "Feinmechanik"),
    R0114(51.023528, 7.562277, 0, "Schlosserei"),
    R0115(51.023605, 7.562117, 0, "Gerätelager"),
    R0116(51.023578, 7.562115, 0, "Lackierraum"),
    R0117(51.023555, 7.562114, 0, "Zentraldepot Putzmittel"),
    R0118(51.023529, 7.562127, 0, "Kraftstofflager"),
    R0119(51.023479, 7.562139, 0, "ZVB"),
    R0120(51.023428, 7.562131, 0, "Materialausgabe"),
    R0121(51.023394, 7.562146, 0, "Material-Ersatzteillager"),
    R0122(51.023325, 7.562144, 0, "ZBV Dokumentation Kartenraum"),
    R0123(51.023301, 7.562188, 0, "UV/AV"),
    R0124(51.023278, 7.562161, 0, "D-WC"),
    R0126(51.023242, 7.562184, 0, "H-WC"),
    R0128(51.023189, 7.562170, 0, "Dusche H"),
    R0130(51.023136, 7.562188, 0, "Dusche D"),
    R0132(51.023319, 7.562066, 0, "Zentrales Lager"),
    R0133(51.023319, 7.562066, 0, "Dauerarchiv"),
    R0134(51.023299, 7.561924, 0, "Archiv SG 1.4"),
    R0135(51.023186, 7.561883, 0, "Sib/Batterie"),
    R0136(51.023184, 7.561949, 0, "Fla/BMA"),
    R0137(51.023181, 7.562000, 0, "EDV HV"),
    R0138(51.023184, 7.562074, 0, "NS HV"),
    R0139(51.023051, 7.562028, 0, "Foyer EG"),

    //Hauptgebäude West-Trakt x.2xx
    R3200(51.023260, 7.561839, 3, "SG-Prof"),
    R3201(51.023299, 7.561845, 3, "ADV-Büro Mitarb."),
    R3202(51.023341, 7.561845, 3, "ADV Lager"),
    R3203(51.023365, 7.561851, 3, "Putzmittel"),
    R3204(51.023374, 7.561667, 3, "ADV-Workstationraum"),
    R3205(51.023287, 7.561679, 3, "ADV-Serverraum"),
    R3206(51.023229, 7.561682, 3, "ADV-Labor"),
    R3207(51.023174, 7.561723, 3, "ADV-Büro Dispatch"),
    R3208(51.023133, 7.561699, 3, "ADV-Büro Servertech."),
    R3209(51.023077, 7.561696, 3, "TDI-Labor 1 (I + II)"),
    R3210(51.022922, 7.561763, 3, "D-WC"),
    //R3211(, 3, ""),
    R3212(51.022920, 7.561718, 3, "H-WC"),
    //R3213(, 3, ""),
    R3214(51.022920, 7.561685, 3, "UV/AV"),
    R3215(51.022850, 7.561716, 3, "TDI-Labor 2 (III + IV)"),
    R3216(51.022713, 7.561716, 3, "MI-Studio"),
    R3217(51.022607, 7.561710, 3, "MI-Multimediaraum"),
    R3218(51.022550, 7.561723, 3, "MI-Tonstudio"),
    R3219(51.022481, 7.561737, 3, "PI-Labor"),
    R3220(51.022382, 7.561732, 3, "PI-Laborraum"),
    R3221(51.022278, 7.561753, 3, "KTDS-Labor I"),
    R3222(51.022139, 7.561764, 3, "KTDS-Labor II"),
    R3223(51.022054, 7.561947, 3, "IDF-Direktor Besprechung"),
    R3224(51.022094, 7.561942, 3, "IDF-Mitarbeiter"),
    R3225(51.022124, 7.561940, 3, "IDF-Prof."),
    R3226(51.022162, 7.561936, 3, "KTDS-Prof."),
    R3227(51.022190, 7.561933, 3, "KTDS-Mitarb."),
    R3228(51.022226, 7.561933, 3, "Direktor / Besprechung"),
    R3229(51.022260, 7.561935, 3, "Gastprofessoren"),
    R3230(51.022294, 7.561929, 3, "Matheprofessoren"),
    R3231(51.022321, 7.561920, 3, "PI-Mitarb."),
    R3232(51.022357, 7.561917, 3, "PI-Prof."),
    R3233(51.022378, 7.561916, 3, "MI-Prof."),
    R3234(51.022424, 7.561919, 3, "MI-Prof."),
    R3235(51.022461, 7.561913, 3, "MI-Videoraum"),
    R3236(51.022511, 7.561921, 3, "UV/AV"),
    R3237(51.022591, 7.561900, 3, "Teeküche"),
    R3238(51.022621, 7.561891, 3, "KTDS Zugang Dach"),
    R3239(51.022667, 7.561896, 3, "MI-Ausleihraum"),
    R3240(51.022716, 7.561881, 3, "MI-Mitarb"),
    R3241(51.022767, 7.561873, 3, "MI-Projektraum"),
    R3242(51.022801, 7.561879, 3, "TDI-Prof."),
    R3243(51.022844, 7.561873, 3, "TDI-Projekt Werkstatt"),
    R3244(51.022877, 7.561875, 3, "TDI-Mitarb."),
    R3245(51.022909, 7.561875, 3, "SG-Seminar Projekt"),
    R3246(51.022941, 7.561878, 3, "Projektraum"),
    R3247(51.022977, 7.561877, 3, "SG-Projekt (PC-Pool)"),
    R3248(51.023017, 7.561869, 3, "SG-Mitarb."),

    R2200(51.023286, 7.561836, 2, "Werkstatt / Lager"),
    R2201(51.023338, 7.561829, 2, "Messlabor II"),
    R2202(51.023356, 7.561680, 2, "Schwerpunkt III CAE"),
    R2203(51.023319, 7.561678, 2, "Software II PC-Pool Electronic"),
    R2204(51.023276, 7.561681, 2, "Schwerpunkt II Messtech. u. Kfz"),
    R2205(51.023218, 7.561684, 2, "Hardware II Digitaltechnik"),
    R2206(51.023110, 7.561679, 2, "Software I PC-Pool Electronic"),
    R2207(51.023024, 7.561704, 2, "Hardware I Grundlagen"),
    R2208(51.022922, 7.561763, 2, "D-WC"),
    //R2209(, 2, ""),
    R2210(51.023242, 7.562184, 2, "H-WC"),
    //R2211(, 2, ""),
    R2212(51.022511, 7.561921, 2, "UV/AV"),
    R2213(51.022883, 7.561760, 2, "Messlabor I (Forschung)"),
    R2214(51.022823, 7.561732, 2, "Hardware II Analogtechnik"),
    R2215(51.022766, 7.561761, 2, "Schwerpunkt I Optoelektronik"),
    R2216(51.022709, 7.561734, 2, "Labor für Energietechnik"),
    R2217(51.022628, 7.561721, 2, "Physik Seminarraum"),
    R2218(51.022556, 7.561736, 2, "Physiklabor II"),
    R2219(51.022477, 7.561748, 2, "Physiklabor I"),
    R2220(51.022387, 7.561750, 2, "Physik Vorbereitung/ Sammlung"),
    R2221(51.022338, 7.561753, 2, "Labor für Arbeitswissenschaften"),
    R2222(51.022271, 7.561757, 2, "BIG-Moderationsraum"),
    R2223(51.022211, 7.561766, 2, "WI-Moderationsraum"),
    R2224(51.022146, 7.561779, 2, "WI-Projektraum (PC-Pool)"),
    R2225(51.022063, 7.561946, 2, "WI-Mitarb."),
    R2226(51.022098, 7.561950, 2, "WI-Mitarb."),
    R2227(51.022126, 7.561939, 2, "WI-Lagerr."),
    R2228(51.022151, 7.561944, 2, "WI-Prof. II"),
    R2229(51.022182, 7.561938, 2, "WI-Besprech."),
    R2230(51.022226, 7.561923, 2, "WI-Prof. I"),
    R2231(51.022285, 7.561910, 2, "WI-Prof. I"),
    R2232(51.022321, 7.561910, 2, "BIG-Prof."),
    R2233(51.022341, 7.561915, 2, "BIG-Direktor Besprechung"),
    R2234(51.022379, 7.561912, 2, "BIG-Prof."),
    R2235(51.022417, 7.561894, 2, "BIG-Prof."),
    R2236(51.022444, 7.561900, 2, "BIG-Prof."),
    R2237(51.022478, 7.561905, 2, "BIG-Prof."),
    R2238(51.022489, 7.561914, 2, "PHY-Mitarb."),
    R2239(51.022514, 7.561922, 2, "UV/AV"),
    R2240(51.022596, 7.561881, 2, "Teeküche"),
    R2241(51.022619, 7.561877, 2, "Putzmittel"),
    R2242(51.022649, 7.561886, 2, "PHY-Direktor Besprechung"),
    R2243(51.022675, 7.561887, 2, "PHY-Prof."),
    R2244(51.022707, 7.561890, 2, "PHY-Prof."),
    R2245(51.022739, 7.561892, 2, "TD-Mitarb."),
    R2246(51.022762, 7.561881, 2, "TD-Prof."),
    R2247(51.022792, 7.561881, 2, "ELE-Prof. (Forschung)"),
    R2248(51.022844, 7.561886, 2, "ELE-Direktor Besprechung"),
    R2249(51.022882, 7.561877, 2, "ELE-Prof. (Forschung)"),
    R2250(51.022909, 7.561879, 2, "ELE-Prof."),
    R2251(51.022951, 7.561860, 2, "ELE-Mitarb."),
    R2252(51.022979, 7.561868, 2, "ELE-Mitarb."),
    R2253(51.023022, 7.561865, 2, "ELE-Drittmittel"),

    R1201(51.023276, 7.561845, 1, "Verteilerraum EDV"),
    R1202(51.023339, 7.561828, 1, "ZBV Verwaltung"),
    R1203(51.023363, 7.561828, 1, "Putzmittel"),
    R1204(51.023364, 7.561664, 1, "Labor für Technomechanik"),
    R1205(51.023216, 7.561692, 1, "Robotertechnologie"),
    R1206(51.023109, 7.561701, 1, "Labor für Werkstoffprüfung"),
    R1207(51.023023, 7.561726, 1, "ZBV"),
    R1208(51.022922, 7.561763, 1, "D-WC"),
    //R1209(, 1, ""),
    R1210(51.023242, 7.562184, 1, "H-WC"),
    //R1211(, 1, ""),
    //R1212(, 1, ""),
    R1213(51.022857, 7.561727, 1, "Labor für Werkstoffprüfung"),
    R1214(51.022799, 7.561733, 1, "Labor für Härteprüfung"),
    R1215(51.022751, 7.561728, 1, "Schleifraum"),
    R1216(51.022691, 7.561740, 1, "Chemie-Labor"),
    R1217(51.022616, 7.561748, 1, "Sammlung Chemie"),
    R1218(51.022558, 7.561746, 1, "Prozessleittechnik"),
    R1219(51.022516, 7.561744, 1, "Verteilerraum EDV"),
    R1220(51.022443, 7.561742, 1, "Industrielle Kommunikation"),
    R1221(51.022385, 7.561734, 1, "Telematik-Labor"),
    R1222(51.022335, 7.561810, 1, "Serverraum"),
    R1223(51.022285, 7.561805, 1, "Automatisierungstechnik"),
    R1224(51.022212, 7.561811, 1, "Antriebstechnik"),
    R1225(51.022109, 7.561810, 1, "Regelungstechnik"),
    R1226(51.022072, 7.561927, 1, "Drittmittel Automation"),
    R1227(51.022104, 7.561940, 1, "AUT-Prof."),
    R1228(51.022141, 7.561931, 1, "AUT-Prof."),
    R1229(51.022198, 7.561909, 1, "AUT-Mitar."),
    R1230(51.022189, 7.561925, 1, "AUT-Mitar."),
    R1231(51.022247, 7.561931, 1, "AUT-Drittmittelkräfte"),
    R1232(51.022282, 7.561925, 1, "AUT-Drittmittelkräfte"),
    R1233(51.022329, 7.561916, 1, "AUT-Institutsdirektor"),
    R1234(51.022350, 7.561919, 1, "AUT"),
    R1235(51.022441, 7.561918, 1, "AUT / ELE Moderationsrau"),
    R1236(51.022489, 7.561914, 1, "Kopierer"),
    R1237(51.022514, 7.561922, 1, "UV/AV"),
    R1238(51.022596, 7.561900, 1, "Teeküche"),
    R1239(51.022625, 7.561891, 1, "Geräteraum"),
    R1240(51.022663, 7.561898, 1, "Lehrkraft"),
    R1241(51.022710, 7.561900, 1, "Verbundstudium"),
    R1242(51.022739, 7.561885, 1, "Drittmittelkräfte"),
    R1243(51.022784, 7.561885, 1, "Lehrkraft"),
    R1244(51.022830, 7.561871, 1, "Rasterelektronenmikroskop/ Ultraschall"),
    R1245(51.022874, 7.561882, 1, "Mathe Prof."),
    R1246(51.022920, 7.561882, 1, "Mathe Prof."),
    R1247(51.022949, 7.561870, 1, "WK-Direktor"),
    R1248(51.022983, 7.561879, 1, "WK Prof."),
    R1249(51.023021, 7.561858, 1, "WK-Mitar."),

    //R0200(, 1, ""),
    R0201(51.023372, 7.561836, 1, "Putzmittel"),
    R0202(51.023172, 7.561707, 1, "Lüftungszentrale"),
//    R0203(, 1, ""),
//    R0204(, 1, ""),
//    R0205(, 1, ""),
//    R0206(, 1, ""),
//    R0207(, 1, ""),
    R0208(51.022862, 7.561702, 1, "CAD PC-Pool"),
    R0209(51.022709, 7.561712, 1, "Labor für Hydraulik"),
    R0209a(51.022611, 7.561719, 1, "Labor für Thermodynamik und Energietechnik"),
    R0210(51.022423, 7.561732, 1, "Labor für autom. Fertigung"),
    R0211(51.022319, 7.561749, 1, "Werkzeugverwaltung / Labor"),
    R0212(51.022238, 7.561800, 1, "Labor für Kunststoffmaschinen"),
    R0213(51.022222, 7.561934, 1, "Labor für Qualitätssicherung"),
    R0214(51.022317, 7.561930, 1, "Projektraum"),
    R0215(51.022434, 7.561923, 1, "PC-Pool Autom. Fertigung"),
    R0216(51.022469, 7.561906, 1, "Serviceraum"),
    R0217(51.022510, 7.561916, 1, "UV/AV"),
    R0218(51.022591, 7.561893, 1, "Teeküche"),
    R0219(51.022611, 7.561904, 1, "Putzmittel"),
    R0220(51.022663, 7.561898, 1, "PPQ-Direktor Besprechung"),
    R0221(51.022703, 7.561903, 1, "Büro PPQ Prof."),
    R0222(51.022741, 7.561897, 1, "Büro PPQ Prof."),
    R0223(51.022771, 7.561892, 1, "Büro PPQ Prof."),
    R0224(51.022801, 7.561881, 1, "Büro PPQ Mitar."),
    R0225(51.022838, 7.561879, 1, "Büro CAD Mitar."),
    R0226(51.022904, 7.561870, 1, "CAD-Labor Projektraum"),
    R0227(51.022996, 7.561861, 1, "Ruheraum"),
    R0228(51.023030, 7.561858, 1, "Büro PPQ-Mitar."),

    //Block Mensa x.3xx + x.4xx
    R0300(51.0220919, 7.562135, 0, "Mensa"),
    R0301(51.022124, 7.562727, 0, "Mensa Essensausgabe"),
    R0401(51.022025, 7.562357, 0, "FERCHAU Saal"),
    R0402(51.022145, 7.562322, 0, "FERCHAU Saal"),
    R0405(51.022318, 7.562288, 0, "BPW Saal"),
    R1400(51.022318, 7.562288, 1, "OPITZ CONSULTING Saal"),
    R1301(51.022124, 7.562727, 1, "Bib"),

    //Ferchau-Gebäude LC6 x.5xx
    R0501(51.0242258, 7.5629291, 0, "Hörsaal"),
    R0502(51.0242378, 7.5630502, 0, "Hörsaal"),
    R0503(51.0242153, 7.5630847, 0, "KI-Labor"),
    R0504(51.0242366, 7.5629356, 0, "Moderationsraum"),
    R0505(51.024236, 7.5630662, 0, "Moderationsraum");

    private String name;
    private double latitude, longitude, altitude;

    Room(double latitude, double longitude, double altitude) {
        this(latitude, longitude, altitude, null);
    }

    Room(double latitude, double longitude, double altitude, String name) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }


    public static String[] getAllRooms() {
        Room[] rooms = Room.values();

        String[] allRooms = new String[rooms.length];
        for(int i = 0; i < allRooms.length; i++) {
            allRooms[i] = "R" + rooms[i].getNumberDot() + " (" + rooms[i].getName() + ")";
        }

        return allRooms;
    }

    /**
     * @return Number of this room without the leading R.
     */
    public String getNumber() {
        return name().substring(1);
    }

    /**
     * @return Number of this room without the leading R but with the dot-convention.
     */
    public String getNumberDot() {
        return getNumber().substring(0,1) + "." + getNumber().substring(1);
    }

    /**
     * @return Number of the building in witch this room in in. (e.g. 3=Mensa, ...)
     */
    public String getBuildingNumber() {
        return getNumber().substring(1,2);
    }

    /**
     * @return Name of this room if it has one, {@code null} otherwise.
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * @return Latitude of this room.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return Longitude of this room.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return Altitude of this room. Currently: 0 = EG, 1 = 1.OG, ...
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * @return Location object with lat, long and altitude of this room.
     */
    public Location getLocation() {
        Location location = new Location("Room");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(altitude);

        return location;
    }

    @Override
    @NonNull
    public String toString() {
        return name;

    }
}
