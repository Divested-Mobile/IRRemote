package org.twinone.irremote.providers.lirc;

import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

class IrCode extends LircListable {

    /**
     *
     */
    private static final long serialVersionUID = 6468863758547559248L;
    private final String pronto;

    public IrCode(String name, String pronto) {
        this.name = name;
        this.pronto = pronto;
    }

    public static Remote toRemote(String name, IrCode[] irCodes) {
        Remote r = new Remote();
        r.name = name;
        r.details.type = Remote.TYPE_UNKNOWN;
        int i = 0;
        for (IrCode code : irCodes) {
            int id = i++;
            Button b = new Button(id);
            b.text = code.name;
            b.code = code.pronto;
            r.buttons.add(b);
        }
        return r;
    }

    public Signal getSignal() {
        return SignalFactory.parse(Signal.FORMAT_PRONTO, pronto);
    }

}
