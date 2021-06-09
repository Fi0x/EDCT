package com.fi0x.edct.data.localstorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NameMap
{
    public static final Map<String, String> COMMODITY_NAMES = new HashMap<>();
    public static final ArrayList<String> RARE_NAMES = new ArrayList<>();
    public static final ArrayList<String> IGNORED = new ArrayList<>();

    public static void initializeNames()
    {
        COMMODITY_NAMES.put("Low Temperature Diamonds", "lowtemperaturediamond");
        COMMODITY_NAMES.put("Micro-weave Cooling Hoses", "coolinghoses");
        COMMODITY_NAMES.put("Hardware Diagnostic Sensor", "diagnosticsensor");
        COMMODITY_NAMES.put("Marine Equipment", "marinesupplies");
        COMMODITY_NAMES.put("Energy Grid Assembly", "powergridassembly");
        COMMODITY_NAMES.put("Power Transfer Bus", "powertransferconduits");
        COMMODITY_NAMES.put("Black Box", "usscargoblackbox");
        COMMODITY_NAMES.put("Atmospheric Processors", "atmosphericextractors");
        COMMODITY_NAMES.put("H.E. Suits", "hazardousenvironmentsuits");
        COMMODITY_NAMES.put("Hostages", "hostage");
        COMMODITY_NAMES.put("Occupied Escape Pod", "occupiedcryopod");
        COMMODITY_NAMES.put("Skimmer Components", "skimercomponents");
        COMMODITY_NAMES.put("Agri-Medicines", "agriculturalmedicines");
        COMMODITY_NAMES.put("Guardian Casket", "ancientcasket");
        COMMODITY_NAMES.put("Guardian Orb", "ancientorb");
        COMMODITY_NAMES.put("Guardian Relic", "ancientrelic");
        COMMODITY_NAMES.put("Guardian Tablet", "ancienttablet");
        COMMODITY_NAMES.put("Guardian Totem", "ancienttotem");
        COMMODITY_NAMES.put("Guardian Urn", "ancienturn");
        COMMODITY_NAMES.put("Commercial Samples", "comercialsamples");
        COMMODITY_NAMES.put("Encrypted Data Storage", "encripteddatastorage");
        COMMODITY_NAMES.put("Large Survey Data Cache", "largeexplorationdatacash");
        COMMODITY_NAMES.put("Mollusc Membrane", "m3_tissuesample_membrane");
        COMMODITY_NAMES.put("Mollusc Mycelium", "m3_tissuesample_mycelium");
        COMMODITY_NAMES.put("Mollusc Spores", "m3_tissuesample_spores");
        COMMODITY_NAMES.put("Mollusc Fluid", "m_tissuesample_fluid");
        COMMODITY_NAMES.put("Mollusc Brain Tissue", "m_tissuesample_nerves");
        COMMODITY_NAMES.put("Mollusc Soft Tissue", "m_tissuesample_soft");
        COMMODITY_NAMES.put("Muon Imager", "mutomimager");
        COMMODITY_NAMES.put("Pod Mesoglea", "s6_tissuesample_mesoglea");
        COMMODITY_NAMES.put("Pod Shell Tissue", "s9_tissuesample_shell");
        COMMODITY_NAMES.put("Pod Core Tissue", "s_tissuesample_core");
        COMMODITY_NAMES.put("Pod Surface Tissue", "s_tissuesample_surface");
        COMMODITY_NAMES.put("Small Survey Data Cache", "smallexplorationdatacash");
        COMMODITY_NAMES.put("Land Enrichment Systems", "terrainenrichmentsystems");
        COMMODITY_NAMES.put("Thargoid Biological Matter", "unknownbiologicalmatter");
        COMMODITY_NAMES.put("Thargoid Resin", "unknownresin");
        COMMODITY_NAMES.put("Thargoid Technology Samples", "unknowntechnologysamples");
        COMMODITY_NAMES.put("Rare Artwork", "usscargorareartwork");
        COMMODITY_NAMES.put("Narcotics", "basicnarcotics");
        COMMODITY_NAMES.put("Microbial Furnaces", "heliostaticfurnaces");
        COMMODITY_NAMES.put("Void Opal", "opal");
        COMMODITY_NAMES.put("Trinkets of Hidden Fortune", "trinketsoffortune");
        COMMODITY_NAMES.put("Ancient Artefact", "usscargoancientartefact");
        COMMODITY_NAMES.put("Experimental Chemicals", "usscargoexperimentalchemicals");
        COMMODITY_NAMES.put("Military Plans", "usscargomilitaryplans");
        COMMODITY_NAMES.put("Rebel Transmissions", "usscargorebeltransmissions");
        COMMODITY_NAMES.put("Technical Blueprints", "usscargotechnicalblueprints");
        COMMODITY_NAMES.put("Prototype Tech", "usscargoprototypetech");
        COMMODITY_NAMES.put("Political Prisoners", "politicalprisoner");

        RARE_NAMES.add("aerialedenapple");
        RARE_NAMES.add("alacarakmoskinart");
        RARE_NAMES.add("albinoquechuamammoth");
        RARE_NAMES.add("altairianskin");
        RARE_NAMES.add("alyabodilysoap");
        RARE_NAMES.add("anduligafireworks");
        RARE_NAMES.add("anynacoffee");
        RARE_NAMES.add("apavietii");
        RARE_NAMES.add("aroucaconventualsweets");
        RARE_NAMES.add("azcancriformula42");
        RARE_NAMES.add("bakedgreebles");
        RARE_NAMES.add("baltahsinevacuumkrill");
        RARE_NAMES.add("bankiamphibiousleather");
        RARE_NAMES.add("bastsnakegin");
        RARE_NAMES.add("belalansrayleather");
        RARE_NAMES.add("bluemilk");
        RARE_NAMES.add("buckyballbeermats");
        RARE_NAMES.add("burnhambiledistillate");
        RARE_NAMES.add("cd75catcoffee");
        RARE_NAMES.add("centaurimegagin");
        RARE_NAMES.add("ceremonialheiketea");
        RARE_NAMES.add("cetiaepyornisegg");
        RARE_NAMES.add("cetirabbits");
        RARE_NAMES.add("chameleoncloth");
        RARE_NAMES.add("chateaudeaegaeon");
        RARE_NAMES.add("cherbonesbloodcrystals");
        RARE_NAMES.add("chieridanimarinepaste");
        RARE_NAMES.add("coquimspongiformvictuals");
        RARE_NAMES.add("crystallinespheres");
        RARE_NAMES.add("damnacarapaces");
        RARE_NAMES.add("deltaphoenicispalms");
        RARE_NAMES.add("deuringastruffles");
        RARE_NAMES.add("disomacorn");
        RARE_NAMES.add("duradrives");
        RARE_NAMES.add("eleuthermals");
        RARE_NAMES.add("eraninpearlwhisky");
        RARE_NAMES.add("eshuumbrellas");
        RARE_NAMES.add("esusekucaviar");
        RARE_NAMES.add("ethgrezeteabuds");
        RARE_NAMES.add("fujintea");
        RARE_NAMES.add("galactictravelguide");
        RARE_NAMES.add("geawendancedust");
        RARE_NAMES.add("gerasiangueuzebeer");
        RARE_NAMES.add("giantirukamasnails");
        RARE_NAMES.add("giantverrix");
        RARE_NAMES.add("gomanyauponcoffee");
        RARE_NAMES.add("haidneblackbrew");
        RARE_NAMES.add("harmasilversearum");
        RARE_NAMES.add("havasupaidreamcatcher");
        RARE_NAMES.add("helvetitjpearls");
        RARE_NAMES.add("hip10175bushmeat");
        RARE_NAMES.add("hiporganophosphates");
        RARE_NAMES.add("honestypills");
        RARE_NAMES.add("hr7221wheat");
        RARE_NAMES.add("indibourbon");
        RARE_NAMES.add("jaquesquinentianstill");
        RARE_NAMES.add("jaradharrepuzzlebox");
        RARE_NAMES.add("jarouarice");
        RARE_NAMES.add("jotunmookah");
        RARE_NAMES.add("kachiriginleaches");
        RARE_NAMES.add("kamitracigars");
        RARE_NAMES.add("karetiicouture");
        RARE_NAMES.add("karsukilocusts");
        RARE_NAMES.add("kinagoinstruments");
        RARE_NAMES.add("konggaale");
        RARE_NAMES.add("korrokungpellets");
        RARE_NAMES.add("lavianbrandy");
        RARE_NAMES.add("leestianeviljuice");
        RARE_NAMES.add("lftvoidextractcoffee");
        RARE_NAMES.add("livehecateseaworms");
        RARE_NAMES.add("ltthypersweet");
        RARE_NAMES.add("mechucoshightea");
        RARE_NAMES.add("medbstarlube");
        RARE_NAMES.add("mokojingbeastfeast");
        RARE_NAMES.add("momusbogspaniel");
        RARE_NAMES.add("mukusubiichitinos");
        RARE_NAMES.add("mulachigiantfungus");
        RARE_NAMES.add("nanomedicines");
        RARE_NAMES.add("neritusberries");
        RARE_NAMES.add("ngadandarifireopals");
        RARE_NAMES.add("ngunamodernantiques");
        RARE_NAMES.add("njangarisaddles");
        RARE_NAMES.add("noneuclidianexotanks");
        RARE_NAMES.add("ochoengchillies");
        RARE_NAMES.add("onionhead");
        RARE_NAMES.add("onionheada");
        RARE_NAMES.add("onionheadb");
        RARE_NAMES.add("ophiuchiexinoartefacts");
        RARE_NAMES.add("orrerianviciousbrew");
        RARE_NAMES.add("pantaaprayersticks");
        RARE_NAMES.add("personalgifts");
        RARE_NAMES.add("platinumaloy");
        RARE_NAMES.add("rajukrustoves");
        RARE_NAMES.add("rapabaosnakeskins");
        RARE_NAMES.add("rusanioldsmokey");
        RARE_NAMES.add("sanumameat");
        RARE_NAMES.add("saxonwine");
        RARE_NAMES.add("shanscharisorchid");
        RARE_NAMES.add("soontillrelics");
        RARE_NAMES.add("sothiscrystallinegold");
        RARE_NAMES.add("tanmarktranquiltea");
        RARE_NAMES.add("taurichimes");
        RARE_NAMES.add("thehuttonmug");
        RARE_NAMES.add("thrutiscream");
        RARE_NAMES.add("tiegfriessynthsilk");
        RARE_NAMES.add("tiolcewaste2pasteunits");
        RARE_NAMES.add("toxandjivirocide");
        RARE_NAMES.add("uszaiantreegrub");
        RARE_NAMES.add("utgaroarmillenialeggs");
        RARE_NAMES.add("uzumokulowgwings");
        RARE_NAMES.add("vanayequirhinofur");
        RARE_NAMES.add("vegaslimweed");
        RARE_NAMES.add("vherculisbodyrub");
        RARE_NAMES.add("vidavantianlace");
        RARE_NAMES.add("volkhabbeedrones");
        RARE_NAMES.add("watersofshintara");
        RARE_NAMES.add("wheemetewheatcakes");
        RARE_NAMES.add("witchhaulkobebeef");
        RARE_NAMES.add("wulpahyperboresystems");
        RARE_NAMES.add("wuthielokufroth");
        RARE_NAMES.add("xihecompanions");
        RARE_NAMES.add("yasokondileaf");
        RARE_NAMES.add("zeesszeantglue");
        RARE_NAMES.add("pavoniseargrubs");
        RARE_NAMES.add("lyraeweed");
        RARE_NAMES.add("kamorinhistoricweapons");
        RARE_NAMES.add("holvaduellingblades");
        RARE_NAMES.add("hip41181squid");
        RARE_NAMES.add("gilyasignatureweapons");
        RARE_NAMES.add("motronaexperiencejelly");
        RARE_NAMES.add("masterchefs");
        RARE_NAMES.add("tarachtorspice");
        RARE_NAMES.add("wolf1301fesh");
        RARE_NAMES.add("terramaterbloodbores");
        RARE_NAMES.add("borasetanipathogenetics");
        RARE_NAMES.add("aganipperush");
        RARE_NAMES.add("hip118311swarm");

        IGNORED.add("thargoidtissuesampletype1");
        IGNORED.add("thargoidtissuesampletype2");
        IGNORED.add("thargoidtissuesampletype3");
        IGNORED.add("thargoidtissuesampletype4");
        IGNORED.add("unknownartifact");
        IGNORED.add("unknownartifact2");
        IGNORED.add("unknownartifact3");
        IGNORED.add("s6_tissuesample_cells");
        IGNORED.add("s6_tissuesample_coenosarc");
        IGNORED.add("s_tissuesample_cells");
        RARE_NAMES.add("transgeniconionhead");
        RARE_NAMES.add("p_particulatesample");
        RARE_NAMES.add("animaleffigies");
        RARE_NAMES.add("alieneggs");
        RARE_NAMES.add("advert1");
    }

    public static String convertDBToEDDN(String localName)
    {
        return COMMODITY_NAMES.getOrDefault(localName, localName);
    }

    public static boolean isRare(String eddnName)
    {
        return RARE_NAMES.contains(eddnName);
    }

    public static boolean isIgnored(String eddnName)
    {
        return IGNORED.contains(eddnName);
    }
}
