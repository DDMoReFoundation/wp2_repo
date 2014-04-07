package org.ddmore.mdl.ui.handler;

import org.ddmore.mdl.controller.RunJobOnTES;
import org.ddmore.mdl.mdl.Mcl;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Inject;
import com.google.inject.Provider;
//import org.eclipse.jface.resource.ImageDescriptor;
//import org.eclipse.swt.graphics.ImageData;
//import org.eclipse.ui.console.ConsolePlugin;
//import org.eclipse.ui.console.IConsoleDocumentPartitioner;
//import org.eclipse.ui.console.IConsoleManager;
//import org.eclipse.ui.console.TextConsole;

import eu.ddmore.converter.mdl2nonmem.Mdl2Nonmem;

public class RunWithNONMEMHandler extends AbstractHandler implements IHandler {

    @Inject
    private Provider<EclipseResourceFileSystemAccess2> fileAccessProvider;

    @Inject
    private Mdl2Nonmem generator;

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
        final IFile file = (IFile) activeEditor.getEditorInput().getAdapter(IFile.class);
        if (file != null) {
            if (activeEditor instanceof XtextEditor) {
                ((XtextEditor) activeEditor).getDocument().readOnly(new IUnitOfWork<Boolean, XtextResource>() {

                    public Boolean exec(XtextResource r) throws Exception {
                        if (generator != null) {
                            Mcl mcl = (Mcl) r.getContents().get(0);
                            String dataFileName = generator.getDataSource(mcl);
                            new RunJobOnTES().run(file, dataFileName);
                        }

                        // XXX Is it valid to submit a job with no data?
                        return Boolean.TRUE;
                    }
                });
            }
        }
        return Boolean.FALSE;
    }

    public boolean isEnabled() {
        return true;
    }

}
