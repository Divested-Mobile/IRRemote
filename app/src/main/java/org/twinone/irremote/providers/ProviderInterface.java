package org.twinone.irremote.providers;

import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.io.Transmitter;

public interface ProviderInterface {

    void requestSaveButton(Button button);

    void requestPreviewRemote(Remote remote);

    void performSaveButton(Button button);

    void requestSaveRemote(Remote remote);

    void performSaveRemote(Remote remote);

    String getAction();

    boolean getOrganize();

    Transmitter getTransmitter();

    void onSaveRemote();

    void onRemotePreview();

    void onSaveButton();
}
