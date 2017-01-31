/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.core.settings;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import pt.ua.dicoogle.core.settings.part.DicomServicesImpl;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.datastructs.SOPClass;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.sdk.settings.server.ServerSettingsReader;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class ServerSettingsTest {

    private URL testConfig;
    private URL legacyConfig;

    @Before
    public void init() {
        this.testConfig = this.getClass().getResource("test-config-new.xml");
        this.legacyConfig = this.getClass().getResource("test-config.xml");
    }

    @Test
    public void test() throws IOException {
        // read the settings from our test config file
        ServerSettings settings = ServerSettingsManager.loadSettingsAt(this.testConfig);
        final ServerSettings.Archive ar = settings.getArchiveSettings();
        final ServerSettings.DicomServices dcm = settings.getDicomServicesSettings();
        final ServerSettingsReader.DicomServices.QueryRetrieve qr = dcm.getQueryRetrieveSettings();

        assertTrue(settings instanceof ServerSettingsImpl);

        // assertions follow

        assertEquals("/opt/my-data", ar.getMainDirectory());
        assertEquals(98, ar.getIndexerEffort());
        assertEquals("/opt/my-data/watched", ar.getWatchDirectory());
        assertEquals("dicoogle01", ar.getNodeName());

        assertSameContent(Collections.singleton("lucene"), ar.getDIMProviders());
        assertSameContent(Collections.singleton("filestorage"), ar.getDefaultStorage());

        assertEquals("TEST-STORAGE", dcm.getAETitle());

        // SOP Classes

        // default
        Collection<String> defaultTS = Arrays.asList(
                "1.2.840.10008.1.2", "1.2.840.10008.1.2.1",
                "1.2.840.10008.1.2.4.80", "1.2.840.10008.1.2.4.50");
        assertSameContent(defaultTS, ((DicomServicesImpl)dcm).getDefaultTransferSyntaxes());

        Collection<SOPClass> sopClasses = Arrays.asList(
                new SOPClass("1.2.840.10008.5.1.4.1.1.88.40", Arrays.asList(
                        "1.2.840.10008.1.2", "1.2.840.10008.1.2.1", "1.2.840.10008.1.2.4.80", "1.2.840.10008.1.2.4.50"
                )),
                new SOPClass("1.2.840.10008.5.1.4.1.1.77.1.1", Arrays.asList(
                        "1.2.840.10008.1.2", "1.2.840.10008.1.2.1", "1.2.840.10008.1.2.4.80", "1.2.840.10008.1.2.4.50"
                )),
                new SOPClass("1.2.840.10008.5.1.4.1.1.12.1.1", Arrays.asList(
                        "1.2.840.10008.1.2", "1.2.840.10008.1.2.1", "1.2.840.10008.1.2.5"
                )));
        assertSameContent(sopClasses, dcm.getSOPClasses());

        // QR settings
        assertFalse(qr.isAutostart());
        assertEquals(1033, qr.getPort());
        assertSameContent(Collections.singleton("any"), dcm.getAllowedLocalInterfaces());
        assertSameContent(Collections.singleton("any"), dcm.getAllowedHostnames());
        assertEquals(1, qr.getRspDelay());
        assertEquals(50, qr.getDIMSERspTimeout());
        assertEquals(51, qr.getIdleTimeout());
        assertEquals(52, qr.getAcceptTimeout());
        assertEquals(53, qr.getConnectionTimeout());
        assertEquals(25, qr.getMaxClientAssoc());
        assertEquals(16374, qr.getMaxPDULengthSend());
        assertEquals(16374, qr.getMaxPDULengthReceive());
        assertSameContent(Arrays.asList("1.2.840.10008.1.2.2", "1.2.840.10008.1.2.1"),
                qr.getTransferCapabilities());
        assertSameContent(Collections.singleton("1.2.840.10008.5.1.4.1.2.1.1"),
                qr.getSOPClass());

        // DICOM Storage settings
        assertTrue(dcm.getStorageSettings().isAutostart());
        assertEquals(6777, dcm.getStorageSettings().getPort());

        // Web server settings
        final ServerSettings.WebServer web = settings.getWebServerSettings();
        assertTrue(web.isAutostart());
        assertEquals(8282, web.getPort());
        assertEquals("test.dicoogle.com", web.getAllowedOrigins());
    /*
        Map<String, String> modalityCFind = new HashMap<>();
        modalityCFind.put("find", "Study Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.1.1", "Patient Root Query/Retrieve Info Model");
        modalityCFind.put("1.2.840.10008.5.1.4.1.2.2.1", "Study Root Query/Retrieve Info Model");
        assertEquals(modalityCFind, settings.getModalityFind());
    */
        // complex stuff
        List<MoveDestination> destinations = Arrays.asList(
                new MoveDestination("ANOTHER-STORAGE", "192.168.42.42", 6666, false, "Our test storage"));
        assertSameContent(destinations, dcm.getMoveDestinations());
    }

    @Test
    public void testDefaultSettings() throws IOException {
        // test that all default settings are ok

        // create default settings
        ServerSettings settings = ServerSettingsImpl.createDefault();
        final ServerSettings.Archive ar = settings.getArchiveSettings();
        final ServerSettings.DicomServices dcm = settings.getDicomServicesSettings();
        final ServerSettingsReader.DicomServices.QueryRetrieve qr = dcm.getQueryRetrieveSettings();

        assertTrue(settings instanceof ServerSettingsImpl);

        // assertions follow

        assertEquals(null, ar.getMainDirectory());
        assertEquals(100, ar.getIndexerEffort());
        assertEquals("", ar.getWatchDirectory());
        assertEquals(null, ar.getNodeName());

        assertEquals("DICOOGLE-STORAGE", dcm.getAETitle());

        // QR settings
        assertTrue(qr.isAutostart());
        assertEquals(1045, qr.getPort());
        assertSameContent(Collections.singleton("any"), dcm.getAllowedLocalInterfaces());
        assertSameContent(Collections.singleton("any"), dcm.getAllowedHostnames());
        assertEquals(0, qr.getRspDelay());
        assertEquals(60, qr.getDIMSERspTimeout());
        assertEquals(60, qr.getIdleTimeout());
        assertEquals(60, qr.getAcceptTimeout());
        assertEquals(60, qr.getConnectionTimeout());
        assertEquals(20, qr.getMaxClientAssoc());
        assertEquals(16364, qr.getMaxPDULengthSend());
        assertEquals(16364, qr.getMaxPDULengthReceive());
        assertSameContent(Arrays.asList("1.2.840.10008.1.2", "1.2.840.10008.1.2.2", "1.2.840.10008.1.2.1"),
                qr.getTransferCapabilities());
        assertSameContent(Arrays.asList("1.2.840.10008.5.1.4.1.2.2.1", "1.2.840.10008.5.1.4.1.2.1.1"),
                qr.getSOPClass());

        // DICOM Storage settings
        assertTrue(dcm.getStorageSettings().isAutostart());
        assertEquals(6666, dcm.getStorageSettings().getPort());

        // Web server settings
        final ServerSettings.WebServer web = settings.getWebServerSettings();
        assertTrue(web.isAutostart());
        assertEquals(8080, web.getPort());
        assertEquals(null, web.getAllowedOrigins());

        assertTrue(dcm.getMoveDestinations().isEmpty());
    }


    @Test
    public void testSave() throws IOException {
        Path target = Files.createTempFile("conf", ".xml");

        // Create default settings
        ServerSettings settings = ServerSettingsImpl.createDefault();

        assertTrue(settings instanceof ServerSettingsImpl);

        settings.getArchiveSettings().setNodeName("dicoogle17");
        settings.getArchiveSettings().setDefaultStorage(Arrays.asList("filestorage", "dropbox"));
        MoveDestination md = new MoveDestination("MORE-TESTING", "192.168.0.55",
                6060, true, "DESCRIPTION");
        settings.getDicomServicesSettings().addMoveDestination(md);
        settings.getDicomServicesSettings().getStorageSettings().setPort(6767);

        ServerSettingsManager.saveSettingsTo(settings, target);

        // read from file
        settings = ServerSettingsManager.loadSettingsAt(target);
        assertEquals("dicoogle17", settings.getArchiveSettings().getNodeName());
        assertSameContent(Arrays.asList("filestorage", "dropbox"),
                settings.getArchiveSettings().getDefaultStorage());
        assertEquals(6767, settings.getDicomServicesSettings().getStorageSettings().getPort());

        Collection<MoveDestination> destinations = Collections.singleton(md);
        assertSameContent(destinations, settings.getDicomServicesSettings().getMoveDestinations());

        boolean removed = settings.getDicomServicesSettings().removeMoveDestination(md.getAETitle());
        assertTrue(removed);
        assertTrue(settings.getDicomServicesSettings().getMoveDestinations().isEmpty());

        // clean up
        Files.delete(target);
    }

    @Test
    public void testMigrate() throws IOException {
        Path newconf = Files.createTempFile("conf", ".xml");

        // load legacy server settings
        ServerSettings settings = ServerSettingsManager.loadLegacySettingsAt(this.legacyConfig);
        assertTrue(settings instanceof LegacyServerSettings);

        // save as new
        ServerSettingsManager.saveSettingsTo(settings, newconf);

        // check new file
        settings = ServerSettingsManager.loadSettingsAt(newconf);
        assertTrue(settings instanceof ServerSettingsImpl);
        ServerSettings.Archive a = settings.getArchiveSettings();

        // assertions follow
        assertEquals("/opt/dicoogle/repository", a.getMainDirectory());
        assertEquals("/tmp", a.getWatchDirectory());
        assertEquals(97, a.getIndexerEffort());
        assertEquals("dicoogle-old", a.getNodeName());

        assertEquals("TEST-STORAGE", settings.getDicomServicesSettings().getAETitle());

        // QR settings
        assertEquals(106, settings.getDicomServicesSettings().getQueryRetrieveSettings().getPort());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedLocalInterfaces());
        assertSameContent(Collections.singleton("any"), settings.getDicomServicesSettings().getAllowedHostnames());
        assertEquals(3, settings.getDicomServicesSettings().getQueryRetrieveSettings().getRspDelay());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getDIMSERspTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getIdleTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getAcceptTimeout());
        assertEquals(50, settings.getDicomServicesSettings().getQueryRetrieveSettings().getConnectionTimeout());
        //assertEquals("1.2.840.10008.5.1.4.1.2.1.1", settings.getSOPClasses());
        assertEquals(22, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxClientAssoc());
        assertEquals(16360, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthSend());
        assertEquals(16360, settings.getDicomServicesSettings().getQueryRetrieveSettings().getMaxPDULengthReceive());

        // DICOM Storage settings
        assertFalse(settings.getDicomServicesSettings().getStorageSettings().isAutostart());
        assertEquals(6666, settings.getDicomServicesSettings().getStorageSettings().getPort());

        // Web server settings
        ServerSettings.WebServer web = settings.getWebServerSettings();
        assertTrue(web.isAutostart());
        assertEquals(8484, web.getPort());
        assertEquals("test.dicoogle.com", web.getAllowedOrigins());

        // clean up
        Files.delete(newconf);
    }

    private static void assertSameContent(Collection o1, Collection o2) {
        assertNotNull("left-hand collection is null", o1);
        assertNotNull("right-hand collection is null", o2);
        for (Object o : o1) {
            if (!o2.contains(o)) {
                throw new ComparisonFailure(null, String.valueOf(o1), String.valueOf(o2));
            }
        }
        for (Object o : o2) {
            if (!o1.contains(o)) {
                throw new ComparisonFailure(null, String.valueOf(o1), String.valueOf(o2));
            }
        }
    }
}