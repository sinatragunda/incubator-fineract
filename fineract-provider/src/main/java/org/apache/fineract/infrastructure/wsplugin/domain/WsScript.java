package org.apache.fineract.infrastructure.wsplugin.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.wsplugin.enumerations.EXECUTION_LEVEL;
import org.apache.fineract.infrastructure.wsplugin.enumerations.RETURN_TYPE;
import org.apache.fineract.infrastructure.wsplugin.enumerations.SCRIPT_TYPE;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 25 April 2023 at 08:08
 */

@Entity
@Table(name = "m_wsscript")
public class WsScript extends AbstractPersistableCustom<Long> {

    @Column(name = "class_name")
    private String qualifiedClassName;

    @Column(name = "method_name")
    private String methodName ;

    @Transient
    private Type type;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "script_type")
    private SCRIPT_TYPE scriptType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "return_type")
    private RETURN_TYPE returnType;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "execution_level")
    private EXECUTION_LEVEL executionLevel;

    @ManyToOne
    @JoinColumn(name="ws_script_container_id")
    private WsScriptContainer wsScriptContainer ;

    protected WsScript(){}

    public WsScript(String qualifiedClassName ,String methodName , Type type) {
        this.qualifiedClassName = qualifiedClassName;
        this.methodName = methodName;
        this.type = type;
        this.returnType = (RETURN_TYPE) EnumTemplateHelper.fromStringEx(RETURN_TYPE.values() ,type.getTypeName());
        this.scriptType = SCRIPT_TYPE.EXTERNAL;
    }

    public WsScript(String qualifiedClassName, String methodName, RETURN_TYPE returnType ,SCRIPT_TYPE scriptType) {
        this.qualifiedClassName = qualifiedClassName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.scriptType = scriptType;
        this.executionLevel = EXECUTION_LEVEL.FIELD;
    }

    public SCRIPT_TYPE getScriptType() {
        return scriptType;
    }

    public WsScriptContainer getWsScriptContainer() {
        return wsScriptContainer;
    }

    public void setWsScriptContainer(WsScriptContainer wsScriptContainer) {
        this.wsScriptContainer = wsScriptContainer;
    }

    public String getQualifiedClassName() {
        return qualifiedClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public RETURN_TYPE getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return "WsScript{" +
                "qualifiedClassName='" + qualifiedClassName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", type=" + type +
                ", scriptType=" + scriptType +
                ", returnType=" + returnType +
                ", wsScriptContainer=" + wsScriptContainer +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WsScript)) return false;
        WsScript wsScript = (WsScript) o;
        return Objects.equals(qualifiedClassName, wsScript.qualifiedClassName) && Objects.equals(methodName, wsScript.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedClassName, methodName);
    }
}
