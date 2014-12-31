<?php

define('INC_DIR', dirname(__DIR__));
require_once(INC_DIR.'/inc/httpjson.inc.php');
require_once(INC_DIR.'/inc/auth.inc.php');

// header('Expires: '.gmdate('D, d M Y H:i:s \G\M\T', time() + 3600));
// header('Cache-Control:max-age='.(3600*24*28));

$req = hj_request();
auth_request($req);

if (empty($req->manufacturer)) {
	$manuf = array("ABC", "ABS", "Accurian", "Accutek", "ADC", "Adcom", "Admiral", "Advent", "Adventura", "Aiko", "Aiwa", "Akai", "Albatron", "Alco", "Alienware", "Allegro", "AlphaStar", "Altec Lansing", "Amana", "Ambassador", "America Action", "American High", "Americast", "Amino", "Amphion Media Works", "Ampro", "AMW", "Anam", "Anam National", "AOC", "Apex Digital", "Apple", "Arrgo", "Asha", "Aspire Digital", "Audio Access", "Audiotronic", "Audiovox", "Aventura", "Axion", "B & K", "Beaumark", "Bell & Howell", "Bell South", "BenQ", "Bionaire", "BK", "Blaupunkt", "Blue Parade", "Bose", "Bradford", "Broksonic", "Burmester", "California Audio Labs", "Calix", "Cambridge Soundworks", "Candle", "Canon", "Capetronic", "Carnivale", "Carver", "CCE", "Celebrity", "Celera", "Century", "Changhong", "Chaparral", "Cinea", "CineVision", "Citizen", "Clairtone", "Clarion", "Classic", "Clearmaster", "ClearMax", "Coby", "Colt", "Comfortex", "Commercial Solutions", "Contec", "Coolmax", "Craig", "Creative", "Crosley", "Crossdigital", "Crown", "Curtis Mathes", "CXC", "CyberHome", "Cybernex", "CyberPower", "Cytron", "D-Link", "Da-Lite", "Daewoo", "Dell", "Delphi", "Denon", "Denstar", "Desay", "Digeo", "Digi", "Director", "DirecTV", "Disco Vision", "Dish Network System", "Dishpro", "Disney", "DKK", "DMX Electronics", "Dual", "Dumont", "Durabrand", "DVD2000", "Dwin", "Dynatech", "Echostar", "Elan", "Electroband", "Electrograph", "Electrohome", "Electrophonic", "Emerex", "Emerson", "Enterprise", "Envision", "Epson", "ESA", "Escient", "Expressvu", "Fisher", "Frigidaire", "Fuji", "Fujitsu", "Funai", "Futuretech", "Garrard", "Gateway", "GE", "General Instrument", "Genexxa", "Gibralter", "Glory Horse", "Go Video", "Go Vision", "GOI", "GoldStar", "Gradiente", "Greenhill", "Grunpy", "Haier", "Hallmark", "Hamlin", "Hannspree", "Harley Davidson", "Harman/Kardon", "Harvard", "Harwood", "Havermy", "Helios", "Hello Kitty", "Hewlett Packard", "HI-Q", "Hisense", "Hitachi", "Hiteker", "Holmes", "Howard Computers", "HP", "HTS", "Hughes Network Systems", "Humax", "Hunter Douglas", "Hush", "Hyundai", "i3 Micro", "iBUYPOWER", "iLo", "Infinity", "Initial", "Insignia", "Integra", "Inteq", "JBL", "JCB", "Jensen", "Jerrold", "JSI", "JVC", "jWin", "Kawasaki", "KDS", "KEC", "Kenmore", "Kenwood", "KLH", "Kodak", "Konka", "KoolConnect", "Koss", "Kost", "Krell", "KTV", "Landel", "Lasko", "Lasonic", "Left Coast", "Lenoxx", "LG", "Lightolier", "Linksys", "Linn", "Liquid Video", "LiteOn", "LiteTouch", "Lloyd's", "Loewe", "Logik", "Lutron", "LXI", "MAG", "Magnasonic", "Magnavox", "Magnin", "Malata", "Marantz", "Marta", "Matsushita", "Maxent", "McIntosh", "MCS", "Media Center PC", "Megapower", "Megatron", "MEI", "Memorex", "MGA", "MGN Technology", "Microsoft", "Midland", "Mind", "Minolta", "Mintek", "Miro", "Mission", "Mitsubishi", "Momitsu", "Monivision", "Motorola", "MTC", "Multitech", "Myrio", "NAD", "NaviPod", "NEC", "Nesa", "Netgear", "NetTV", "Next Base", "Next Level", "NexxTech", "Nikko", "Niveus Media", "Noblex", "Norcent", "Northgate", "Nova", "NSM", "NTC", "Olevia", "Olympus", "Omnifi", "One For All", "Onkyo", "Onwa", "Optimus", "Optoma", "OptoMedia Electronics", "Optonica", "Orion", "Oritron", "Pace", "Panasonic", "Panther", "Paragon", "Parasound", "Paysat", "PCS", "Penney", "Pentax", "Petters", "Philco", "Philips", "Pilot", "Pioneer", "Polaroid", "Polk Audio", "Portland", "Presidian", "Prima", "Princeton", "Prism", "Profitronic", "Proscan", "Protec", "Proton", "Proview", "ProVision", "Pulsar", "QED", "Quad", "Quasar", "Qwestar", "RadioShack", "Radix", "Randex", "RCA", "Realistic", "Regal", "ReplayTV", "Rhapsody", "Ricavision", "Rio", "Roku", "Rotel", "Rowa", "Royal Sovereign", "Runco", "Russound", "SAE", "Sampo", "Samsung", "Sanky", "Sansui", "Sanyo", "Sceptre", "Scientific Atlanta", "Scotch", "Scott", "Sears", "Security System", "Sega", "Sejin", "Sensory Science", "Sharp", "Sharper Image", "Sheng Chia", "Sherwood", "Shinsonic", "Shintom", "Shogun", "Signet", "Simpson", "Singer", "Sirius", "Slim Devices", "SmartLinc", "SMC", "Somfy", "Sonic Blue", "Sonic Frontiers", "Sony", "Soundesign", "Sova", "Spectroniq", "Squareview", "SSS", "Stack 9", "Star Choice", "Starcom", "Starlite", "Stereophonics", "STS", "Studio Experience", "Sunfire", "Supercable", "Supermax", "Superscan", "Supreme", "SureWest", "SVA", "Sylvania", "Symphonic", "Syntax", "Systemax", "TAG McLaren", "Tagar Systems", "Tandy", "Tascam", "Tatung", "Teac", "Technics", "Technosonic", "Techview", "Techwood", "Teknika", "Telefunken", "Theta Digital", "Thomas", "Thomson", "Thorens", "Tivo", "TMK", "TNCi", "Tocom", "Torx", "Toshiba", "Tosonic", "Totevision", "Touch", "Tredex", "Tristar", "TVS", "UltimateTV", "Uniden", "Unitech", "Universal X10", "Urban Concepts", "US Digital", "US Logic", "USDTV", "V2", "Vector", "Vector Research", "Venturer", "Victor", "Video Concepts", "Videomagic", "Videosonic", "Vidikron", "Vidtech", "Viewmaster", "Viewsonic", "Villain", "Vision", "Vizio", "Voodoo", "Voom", "Vortex View", "Wards", "Waycon", "Westinghouse", "Whirlpool", "White Westinghouse", "Windmere", "Wyse", "X10", "Xbox", "XM", "XR-1000", "Yamaha", "Zenith", "Zoece", "ZT Group");
	hj_resp('manufacturers', $manuf);
	hj_return(0);
}
else if (empty($req->device_type)) {
	$dt = array($req->manufacturer, "test", "some other device type", "tv", "a");
	hj_resp('device_types', $dt);
	hj_return(0);
}
else if (empty($req->device)) {
	$devices = array(
		array('author' => "Twinone"   , 'size' => 32, 'name' => 'Test 1', 'id' => 0),
		array('author' => "12345"     , 'size' => 30, 'name' => 'Test 2', 'id' => 1),
		array('author' => "nobody"    , 'size' => 20, 'name' => 'Test 3', 'id' => 2),
		array('author' => "Anonymous" , 'size' => 11, 'name' => 'Test 4', 'id' => 3),
		array('author' => "Luukje"    , 'size' => 1,  'name' => 'Test 5', 'id' => 4)
		);
	hj_resp('devices', $devices);
	hj_return(0);
}
else { // ir codes
	
	hj_resp('remote', $ir_codes);
}

?>