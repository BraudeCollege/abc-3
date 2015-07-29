/*
 * ElementList.java
 * Copyright (C) 2015 Sietse Ringers
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

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

	public static ElementList random(int n) {
		ElementList l = new ElementList(n);
		Field Zn = SystemParameters.e.getZr();

		for (int i = 0; i < n; i++) {
			l.add(Zn.newRandomElement());
		}

		return l;
	}
}
