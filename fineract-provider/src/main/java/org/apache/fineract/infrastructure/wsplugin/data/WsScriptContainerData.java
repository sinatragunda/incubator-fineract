/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 27 April 2023 at 08:28
 */
package org.apache.fineract.infrastructure.wsplugin.data;

import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.utility.service.EnumeratedData;

import java.util.Collection;

public class WsScriptContainerData implements EnumeratedData {

    private Long id;
    private String name ;
    private String description ;
    private String fileName ;
    private String fileLocation ;
    private Long documentId ;

    private Collection<WsScriptData> wsScriptDataCollection;

    public WsScriptContainerData(Long id, String name, String description, String fileName, String fileLocation, Long documentId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fileName = fileName;
        this.fileLocation = fileLocation;
        this.documentId = documentId;
    }

    public WsScriptContainerData(String name, Collection<WsScriptData> wsScriptDataCollection) {
        this.name = name;
        this.wsScriptDataCollection = wsScriptDataCollection;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setWsScriptDataCollection(Collection<WsScriptData> wsScriptDataCollection) {
        this.wsScriptDataCollection = wsScriptDataCollection;
    }
}
