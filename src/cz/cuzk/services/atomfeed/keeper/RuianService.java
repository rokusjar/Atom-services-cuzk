package cz.cuzk.services.atomfeed.keeper;

import cz.cuzk.services.atomfeed.config.Source;
import cz.cuzk.services.atomfeed.feed.common.DatasetFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 */
public class RuianService extends Service {

    Boolean csv = false;
    //

    public RuianService(ArrayList<Source> sources, String themeCode, String dateOfChange){
        this.setSources(sources);
        this.setServiceId(themeCode);
        this.setDateOfChange(dateOfChange);
        if(themeCode.contains("CSV")) this.csv = true;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<DatasetFile> getCurrentState() throws UnknownDatasetException {

        ArrayList<DatasetFile> files = new ArrayList<DatasetFile>();

        for(Source source : getSources()){

            File sourceDir = new File(source.getDirPath());

            for(File file : sourceDir.listFiles()){
                
                if(!file.getName().endsWith("csv.gz") && !file.getName().endsWith("csv.zip") &&
                        !file.getName().endsWith("xml.gz") &&!file.getName().endsWith("tar.gz")) {
                    continue;
                }
                String fileName = file.getName();
                RuianFile rFile = new RuianFile(fileName, this.csv);

                if (this.belongToDataset(rFile)) {
                    if(file.isFile()){
                        DatasetFile dFile = new DatasetFile();

                        dFile.setFile_name(file.getName().split("\\.")[0]);
                        dFile.setFile_extension(getFileExtension(file));
                        dFile.setWebPath(source.getWebPath() + "/" + dFile.getFile_name()
                                + "." + dFile.getFile_extension());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy:HH:mm:ss");
                        dFile.setUpdated(sdf.format(file.lastModified()));
                        dFile.setInspire_dls_code(this.dlsCode(file));
                        dFile.setFile_size(Long.toString(file.length()));
                        dFile.setGeorss_type(source.getGeorss_type());
                        dFile.setCrs_epsg(source.getEpsg());
                        if(rFile.getRozsah().equals("ST")){
                            dFile.setUnit_code("1");
                            dFile.setUnit_type("stat");
                        }else{
                            dFile.setUnit_code(file.getName().split("_")[2]);
                            dFile.setUnit_type("obec");
                        }
                        dFile.setMetadata_link("xxx");
                        dFile.setService_id(this.getServiceId());
                        dFile.setFormat(source.getFormat());
                        dFile.setFile_id(fileCode(file, dFile.getCrs_epsg(), dFile.getFormat()));
                        files.add(dFile);
                    }
                }else {
                    continue;
                }
            }
        }
        return files;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * V případě změnových dat je nutné od sebe odlišit jednotlivé soubory, protože jsou generovány za stát a mají
     * proto stejný kód jendotky. z toho důvodu je přidán ještě datum.
     * U stavových dat datum přidáváno není.
     * @param file
     * @return
     */
    @Override
    public String dlsCode(File file) {
        String dlsCode;
        if(getServiceId().equals("RUIAN-S-K-Z") || getServiceId().equals("RUIAN-S-ZA-Z") ||
                getServiceId().equals("RUIAN-H-ZA-Z") ){
            dlsCode = "CZ-00025712-CUZK_" + this.getServiceId().trim() + "_" + unitCode(file) + "-" + date(file);
        }else {
            dlsCode = "CZ-00025712-CUZK_" + this.getServiceId().trim() + "_" + unitCode(file);
        }
        return dlsCode;
    }

    @Override
    public String fileCode(File file, String epsg, String format) {
        String fileCode;
        fileCode = this.dlsCode(file) + "_" + format + "-" + epsg;
        return fileCode;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí kód jednotky. Pokud se jedná o obec, tak vrátí 6 ti místný kód obce.
     * Pokud se jedná o stát, tak vrátí 1 plus něco dalšího aby od sebe byly soubory pro stát odlišitelné.
     * Například vrátí 1-O nebo 1-H písmena O, H a další jsou převzata z popisu VFR.
     * @param file
     * @return
     */
    private String unitCode(File file){
        String unitCode = "neznámý kód";

        if(getServiceId().contains("CSV")){
            String fileName = file.getName().split("\\.")[0];
            if(fileName.contains("strukt")){
                unitCode = "1";
            }
            else if(fileName.split("_").length == 4){
                unitCode = fileName.split("_")[2];
            }
            else if(fileName.split("_").length == 3){
                unitCode = "1";
            }
        }else {

            if (file.getName().split("\\.")[0].contains("ST") && !getServiceId().contains("SP")) {
                // napr 20151231_ST_UZSZ.xml
                unitCode = "1" + "-" + file.getName().split("\\.")[0].split("_")[2].substring(3, 4);
            }

            if (file.getName().split("\\.")[0].contains("OB")) {
                // napr 20151031_OB_500046_UZSZ.xml.gz
                unitCode = file.getName().split("\\.")[0].split("_")[2];
            }

            if (file.getName().split("\\.")[0].contains("ST") && getServiceId().contains("SP")) {
                unitCode = "1" + "-" + file.getName().split("\\.")[0].split("_")[2].substring(2, 4);
            }
        }

        return unitCode;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí příponu souboru.
     * když josu tečky dvě - fileA.csv.gz - vrátí csv.gz
     * když je tečka jedna - fileB.zip - vrátí zip
     * Když to není ani jeden z předchozích dvou případů, tak jméno souboru rozdělí podle teček a vrátí poslední část
     * @param file
     * @return
     */
    private String getFileExtension(File file){

        String fileName = file.getName();
        String[] parts = fileName.split("\\.");

        if(parts.length == 2){
            return parts[1];
        }

        if(parts.length == 3){
            return parts[1] + "." + parts[2];
        }

        return parts[parts.length - 1];
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí datum získané z názvu souboru. Datum je ve formátu RRRR.MM.DD.
     * @param file
     * @return
     */
    private String date(File file){
        return file.getName().split("\\.")[0].split("_")[0];
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí aktuální měsíc jako číslo. Pokud je zrovna listopad vrátí 11.
     * @return
     */
    private Integer getCurrentMonth(){
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.getMonth().getValue();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí aktuální rok jako číslo. Např. 2016
     * @return
     */
    private Integer getCurrentYear(){
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.getYear();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Pokud soubor patří do daného datasetového feedu, tak vrátí true. Pokud do něj neptaří vrátí false a
     * pokud VŮBEC nepozná kód datasetu, vrátí null.
     * @param file
     * @return
     * @throws UnknownDatasetException
     */
    private Boolean belongToDataset(RuianFile file) throws UnknownDatasetException {

        Boolean result = null;

        //SOUCASNA
        if(!this.getServiceId().contains("-SP-") && !this.getServiceId().contains("-H-") && !this.getServiceId().contains("-CSV-")){
            String typDat = this.getServiceId().split("-")[3];
            String typDatoveSady = this.getServiceId().split("-")[2];
            if(typDatoveSady.equals("ZA")){
                typDatoveSady = "Z";
            }else if(typDatoveSady.equals("K")){
                typDatoveSady = "K";
            }else {
                throw new UnknownDatasetException("Trida RuianDir narazila na neznamou sluzbu: " + this.getServiceId());
            }

            if(file.getTypDat().equals(typDat) && file.getTypDatoveSady().equals(typDatoveSady)){
                int previousmonth = this.getCurrentMonth() - 1;
                if(previousmonth == 0) previousmonth = 12;

                if(typDat.equals("U") && file.getMonth() == previousmonth){
                    result = true;
                }else if (typDat.equals("Z") && file.getMonth() == this.getCurrentMonth()){
                    result = true;
                }else {
                    result = false;
                }
            }else {
                result = false;
            }
        }

        //SPECIALNI
        if(this.getServiceId().contains("-SP-")){
            String typDat = this.getServiceId().split("-")[3];
            String typDatoveSady = this.getServiceId().split("-")[2];
            if(typDatoveSady.equals("CIS")){
                typDatoveSady = "C";
            }else if(typDatoveSady.equals("VO")){
                typDatoveSady = "V";
            }else {
                throw new UnknownDatasetException("Trida RuianDir narazila na neznamou sluzbu: " + this.getServiceId());
            }

            if(file.getTypDat().equals(typDat) && file.getTypDatoveSady().equals(typDatoveSady)){
                int previousmonth = this.getCurrentMonth() - 1;
                if(previousmonth == 0) previousmonth = 12;

                if(typDat.equals("U") && file.getMonth() == this.getCurrentMonth()){
                    result = true;
                }else if (typDat.equals("Z") && file.getMonth() == this.getCurrentMonth()){
                    result = true;
                }else {
                    result = false;
                }
            }else {
                result = false;
            }
        }

        //HISTORICKE
        if(this.getServiceId().contains("-H-")){
            String typDat = this.getServiceId().split("-")[3];
            String typDatoveSady = this.getServiceId().split("-")[2];
            if(typDatoveSady.equals("ZA")){
                typDatoveSady = "Z";
            }else if(typDatoveSady.equals("K")){
                typDatoveSady = "K";
            }else {
                throw new UnknownDatasetException("Trida RuianDir narazila na neznamou sluzbu: " + this.getServiceId());
            }

            if(file.getTypDat().equals(typDat) && file.getTypDatoveSady().equals(typDatoveSady)){
                int previousmonth = this.getCurrentMonth() - 1;
                if(previousmonth == 0) previousmonth = 12;

                if(typDat.equals("U") && file.getMonth() == previousmonth){
                    result = true;
                }else if (typDat.equals("Z") && file.getMonth() == this.getCurrentMonth()){
                    result = true;
                }else {
                    result = false;
                }
            }else {
                result = false;
            }
        }

        //CSV
        if(this.getServiceId().contains("-CSV-")){

            String jednotka = this.getServiceId().split("-")[3]; //ST nebo OB
            String typ = this.getServiceId().split("-")[2]; //ADR nebo HIE

            Integer previousmonth = this.getCurrentMonth() - 1;
            Integer year = this.getCurrentYear();
            if(previousmonth == 0) {
                previousmonth = 12;
                year = year - 1;
            }

            if(file.getTypDatoveSady().equals("HIE")){
                if(typ.equals("HIE") && file.getMonth().intValue() == previousmonth.intValue() &&
                        file.getYear().intValue() == year.intValue()){
                    result = true;
                }else{
                    result = false;
                }
            }
            else if(file.getRozsah().equals("ST")){

                if(typ.equals("ADR") && jednotka.equals("ST") && file.getMonth().intValue() == previousmonth.intValue() &&
                        file.getYear().intValue() == year.intValue()){
                    result = true;
                }else{
                    result = false;
                }
            }
            else if(file.getRozsah().equals("OB")){
                if(typ.equals("ADR") && jednotka.equals("OB") && file.getMonth().intValue() == previousmonth.intValue() &&
                        file.getYear().intValue() == year.intValue()){
                    result = true;
                }else{
                    result = false;
                }
            }
            else {
                result = false;
            }
        }
        return result;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vnitřní statická třída.
     */
    public static class RuianFile{

        private String datum;
        private String rozsah;        // stat ST/obec O
        private String kodObce;
        private String typDat;        // uplna U/zmenova Z
        private String typDatoveSady; // zakladni Z/kompletni K /ADR adresy / HIE hierarchie
        private String zdrojDat;      // soucasna S/historicka H
        //--------------------------------------------------------------------------------------------------------------
        public RuianFile(String fileName, Boolean csv){

            if(!csv) {
                setDatum(fileName.split("_")[0]);
                setRozsah(fileName.split("_")[1]);

                if (fileName.split("_").length == 4) {
                    setKodObce(fileName.split("_")[2]);
                    setTypDat(fileName.split("_")[3].substring(0, 1));
                    setTypDatoveSady(fileName.split("_")[3].substring(1, 2));
                    setZdrojDat(fileName.split("_")[3].substring(3));
                } else {
                    setTypDat(fileName.split("_")[2].substring(0, 1));
                    setTypDatoveSady(fileName.split("_")[2].substring(1, 2));
                    setZdrojDat(fileName.split("_")[2].substring(3));
                }
            }else {
                setDatum(fileName.split("_")[0]);

                if(fileName.contains("OB_ADR") || fileName.contains("strukt")){
                    setRozsah("ST");
                    if(fileName.contains("strukt")){
                        setTypDatoveSady("HIE");
                    }else{
                        setTypDatoveSady("ADR");
                    }

                }else{
                    setRozsah("OB");
                    setKodObce(fileName.split("_")[2]);
                    setTypDatoveSady("ADR");
                }
            }

        }
        //--------------------------------------------------------------------------------------------------------------
        /**
         * Z názvu souboru ve kterém je datum ve formátu RRRRMMDD vrátí měsíc jako číslo.
         * Např když 20150910 vrátí 9
         * @return
         */
        public Integer getMonth(){
            return Integer.parseInt(datum.substring(4,6));
        }
        //--------------------------------------------------------------------------------------------------------------
        public Integer getYear(){
            return Integer.parseInt(datum.substring(0,4));
        }
        //--------------------------------------------------------------------------------------------------------------
        public String getZdrojDat() {
            return zdrojDat;
        }

        public void setZdrojDat(String zdrojDat) {
            this.zdrojDat = zdrojDat;
        }

        public String getTypDatoveSady() {
            return typDatoveSady;
        }

        public void setTypDatoveSady(String typDatoveSady) {
            this.typDatoveSady = typDatoveSady;
        }

        public String getTypDat() {
            return typDat;
        }

        public void setTypDat(String typDat) {
            this.typDat = typDat;
        }

        public String getKodObce() {
            return kodObce;
        }

        public void setKodObce(String kod) {
            this.kodObce = kod;
        }

        public String getRozsah() {
            return rozsah;
        }

        public void setRozsah(String rozsah) {
            this.rozsah = rozsah;
        }

        public String getDatum() {
            return datum;
        }

        public void setDatum(String datum) {
            this.datum = datum;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
}
