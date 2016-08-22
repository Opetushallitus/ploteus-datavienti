### Ploteus

#### XSD-generointi
Valitsemassasi IDE:ssä generoit JAXB luokat schemasta.

#### Getting Started
Luo ploteus_generated kansio kotihakemistoosi tai projektia ajavan käyttäjän kotikansioon.
Luo oph-configuration kansio kotihakemistoosi tai projektia ajavan käyttäjän kotikansioon ja luo kansioon common.properties tiedosto (esimerkkitiedostona /ploteus-datavienti/deployment/oph-configuration/ploteus-datavienti.properties.template).

Kyseessä on Spring Boot sovellus.

#### Lokalisointi
Projekti on maven pohjainen. Sen voi importata haluamasi IDE:n kautta.
Sovelluksen output kansion voi vaihtaa common.properties tiedostossa.
Ajettaessa eri ympäristöjä vasten täytyy ympäristökohtaiset osoitteet vaihtaa common.properties tiedostoon.
Ajettaessa projekti käynnistyy osoitteeseen localhost:8080/ploteus


#### Ylläpito
Jos koodisto-apin, tarjonta-apin tai organisaatio-apin versioita tarvitsee päivittää, se tapahtuu pom.xml:ssä muokkaamalla properties tagin versionumeroita.
	
	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<java.version>1.8</java.version>
		<tarjonta.version>2016-07-SNAPSHOT</tarjonta.version>
		<organisaatio.version>2015-13</organisaatio.version>
		<koodisto.version>2016-04-SNAPSHOT</koodisto.version>
		<buildversion_prefix />
	</properties>