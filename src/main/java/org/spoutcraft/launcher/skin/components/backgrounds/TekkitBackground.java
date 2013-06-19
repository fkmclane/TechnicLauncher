/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.skin.components.backgrounds;

import org.spoutcraft.launcher.skin.TechnicLoginFrame;
import org.spoutcraft.launcher.skin.components.AnimatedImage;
import org.spoutcraft.launcher.skin.components.EnhancedBackground;

public class TekkitBackground extends EnhancedBackground {
	private AnimatedImage tekkit;

	public TekkitBackground() {
		super("tekkitmain");
		tekkit = new TekkitCreeper(650, 100, TechnicLoginFrame.getIcon("creeper.png", 107, 69));
		tekkit.setBounds(500, 100, 107, 69);
		tekkit.setVisible(false);
		this.add(tekkit);
	}


	@Override
	public void setVisible(boolean aFlag) {
		tekkit.setAnimating(aFlag);
		tekkit.setVisible(aFlag);
		super.setVisible(aFlag);
	}
}
