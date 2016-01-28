package cz.cuzk.services.atomfeed.keeper;

import cz.cuzk.services.atomfeed.config.Source;
import cz.cuzk.services.atomfeed.feed.common.DatasetFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 */
public class KmVkmService extends Service {

    public KmVkmService(ArrayList<Source> sources, String themeCOde, String dateOfChange){
        this.setSources(sources);
        this.setServiceId(themeCOde);
        this.setDateOfChange(dateOfChange);
    }
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public ArrayList<DatasetFile> getCurrentState() {

        ArrayList<DatasetFile> files = new ArrayList<>();

        for(Source source : this.getSources()) {
            File dir = getRightDir(source);
            for (File file : dir.listFiles()) {
                if (file.isFile()) {

                    if (!file.getName().split("\\.")[1].equals("zip")) {
                        continue;
                    }

                    DatasetFile dFile = new DatasetFile();

                    dFile.setFile_name(file.getName().split("\\.")[0]);
                    dFile.setFile_extension(file.getName().split("\\.")[1]);
                    dFile.setWebPath(source.getWebPath() + "/" + dir.getName() + "/" + dFile.getFile_name()
                            + "." + dFile.getFile_extension());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy:HH:mm:ss");
                    dFile.setUpdated(sdf.format(file.lastModified()));
                    dFile.setInspire_dls_code(this.dlsCode(file));
                    dFile.setFile_size(Long.toString(file.length()));
                    dFile.setUnit_type(source.getUnit_type());
                    dFile.setGeorss_type(source.getGeorss_type());
                    dFile.setCrs_epsg(source.getEpsg());
                    dFile.setUnit_code(file.getName().split("\\.")[0]);
                    dFile.setMetadata_link("xxx");
                    dFile.setService_id(this.getServiceId());
                    dFile.setFormat(source.getFormat());
                    dFile.setFile_id(fileCode(file, dFile.getCrs_epsg(), dFile.getFormat()));

                    files.add(dFile);
                }
            }
        }
        return files;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public String dlsCode(File file) {
        String dlsCOde;
        dlsCOde = "CZ-00025712-CUZK_" + this.getServiceId().trim() + "_" + file.getName().split("\\.")[0];
        return dlsCOde;
    }

    @Override
    public String fileCode(File file, String epsg, String format) {
        String fileCode;
        fileCode = this.dlsCode(file) + "_" + format + "-" + epsg;
        return fileCode;
    }

    /**
     * Z adresářů které se ve složce nachází vybere podle názvu ten nejnovější.
     * @return
     */
    private File getRightDir(Source source){

        File rightDir = null;

        ArrayList<Long> datumy = new ArrayList<Long>();

        for(File f : new File(source.getDirPath()).listFiles()){
            if(f.isDirectory()){
                datumy.add(Long.parseLong(f.getName()));
            }
        }
        Collections.sort(datumy);
        if(datumy.size() == 1){
            rightDir = new File(source.getDirPath() + "\\" + datumy.get(0).toString() + "\\");
        }else {
            rightDir = new File(source.getDirPath() + "\\" + datumy.get(datumy.size() - 1).toString() + "\\");
        }
        return rightDir;
    }
    //------------------------------------------------------------------------------------------------------------------
}
