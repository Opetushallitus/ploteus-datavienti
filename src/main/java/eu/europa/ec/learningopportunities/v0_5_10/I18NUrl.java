//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.20 at 09:04:12 AM EEST 
//


package eu.europa.ec.learningopportunities.v0_5_10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for i18nUrl complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="i18nUrl">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://ec.europa.eu/LearningOpportunities/v0_5_10>customUrl">
 *       &lt;attribute name="language" type="{http://ec.europa.eu/LearningOpportunities/v0_5_10}LanguageCode" default="en" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "i18nUrl", propOrder = {
    "value"
})
public class I18NUrl {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "language")
    protected LanguageCode language;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link LanguageCode }
     *     
     */
    public LanguageCode getLanguage() {
        if (language == null) {
            return LanguageCode.EN;
        } else {
            return language;
        }
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link LanguageCode }
     *     
     */
    public void setLanguage(LanguageCode value) {
        this.language = value;
    }

}
