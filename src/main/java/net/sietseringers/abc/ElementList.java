package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

import java.util.ArrayList;

public class ElementList extends ArrayList<Element> {
	public ElementList duplicate() {
		ElementList r = new ElementList(this.size());

		for (Element el: this) {
			r.add(el.duplicate());
		}

		return r;
	}

	public void add(ElementList l) {
		for (Element el: l) {
			this.add(el.duplicate());
		}
	}

	public ElementList powZn(Element a) {
		for (Element el: this) {
			el.powZn(a);
		}
		return this;
	}

	public ElementList powZn(ElementList ai) {
		for (int i = 0; i < this.size(); i++) {
			this.get(i).powZn(ai.get(i));
		}
		return this;
	}

	public ElementList getImmutable() {
		ElementList l = new ElementList(this.size());
		for (Element el: this)
			l.add(el.getImmutable());
		return l;
	}

	public ElementList() {
		super();
	}

	public ElementList(int n) {
		super(n);
	}

	public static ElementList random(Field f, int n) {
		ElementList l = new ElementList(n);

		for (int i = 0; i < n; i++) {
			l.add(f.newRandomElement());
		}

		return l;
	}
}
