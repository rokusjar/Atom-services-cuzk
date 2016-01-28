package cz.cuzk.services.atomfeed.keeper;

import cz.cuzk.services.atomfeed.config.Source;
import cz.cuzk.services.atomfeed.feed.common.DatasetFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 */
public class InspireService extends Service {

    public InspireService(ArrayList<Source> sources, String themeCode, String dateOfChange){
        this.setSources(sources);
        this.setServiceId(themeCode);
        this.setDateOfChange(dateOfChange);
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<DatasetFile> getCurrentState() {

        ArrayList<DatasetFile> files = new ArrayList<DatasetFile>();

        for(Source source : getSources()){

            File sourceDir = new File(source.getDirPath());

            for(File file : sourceDir.listFiles()){
                if(file.isFile()){

                    if(!file.getName().split("\\.")[1].equals("zip")) {
                        continue;
                    }

                    DatasetFile dFile = new DatasetFile();

                    dFile.setFile_name(file.getName().split("\\.")[0]);
                    dFile.setFile_extension(file.getName().split("\\.")[1]);
                    dFile.setWebPath(source.getWebPath() + "/" + dFile.getFile_name()
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

    @Override
    public String dlsCode(File file) {
        String dlsCode;
        dlsCode = "CZ-00025712-CUZK_" + this.getServiceId().trim() + "_" + file.getName().split("\\.")[0];
        return dlsCode;
    }

    @Override
    public String fileCode(File file, String epsg, String format) {
        String fileCode;
        fileCode = this.dlsCode(file) + "_" + format + "-" + epsg;
        return fileCode;
    }
}
