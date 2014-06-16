package org.twinone.irremote.providers.lirc;

import org.twinone.irremote.Button;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

import android.content.Context;

public class IrCode extends LircListable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6468863758547559248L;
	public String pronto;

	public IrCode(String name, String pronto) {
		this.name = name;
		this.pronto = pronto;
	}

	public Signal getSignal() {
		return SignalFactory.parse(Signal.FORMAT_PRONTO, pronto);
	}

	public static Remote toRemote(Context c, String name, IrCode[] irCodes) {
		Remote r = new Remote();
		r.name = name;
		r.options.type = Remote.TYPE_UNKNOWN;
		int i = 0;
		for (IrCode code : irCodes) {
			Button b = new Button();
			b.text = code.name;
			b.code = code.pronto;
			b.id = i++;
			r.buttons.add(b);
		}
		return r;
	}

}
