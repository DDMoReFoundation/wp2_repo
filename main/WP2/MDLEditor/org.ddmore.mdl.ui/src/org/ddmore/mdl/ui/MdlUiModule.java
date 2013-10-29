/*
 * generated by Xtext
 */
package org.ddmore.mdl.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Use this class to register components to be used within the IDE.
 */
public class MdlUiModule extends org.ddmore.mdl.ui.AbstractMdlUiModule {

    private static final Logger LOG = Logger.getLogger(MdlUiModule.class);

    private static final String MIF_ENCRYPTION_KEY = "mif.encryption.key";

    public MdlUiModule(AbstractUIPlugin plugin) {
        super(plugin);
        init();
    }

    public void init() {
        if (System.getProperty(MIF_ENCRYPTION_KEY) != null) {
            try {
                // FIXME expecting MIF_ENCRYPTION_KEY to contain the path relative to the CWD
                URL url = new URL("file:/" + System.getProperty("user.dir") + "/" + System.getProperty(MIF_ENCRYPTION_KEY));
                LOG.debug(String.format("%s property was set to %s", MIF_ENCRYPTION_KEY, url));
                System.setProperty(MIF_ENCRYPTION_KEY, url.toExternalForm());
            } catch (MalformedURLException e) {
                LOG.error(String.format("%s property was set to and invalid URL", MIF_ENCRYPTION_KEY), e);
            }
        } else {
            LOG.warn(String.format("%s property was not set", MIF_ENCRYPTION_KEY));
        }
    }
}
