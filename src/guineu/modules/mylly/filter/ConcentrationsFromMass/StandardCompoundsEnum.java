/*
 * Copyright 2007-2013 VTT Biotechnology
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.modules.mylly.filter.ConcentrationsFromMass;

/**
 *
 * @author scsandra
 */

public enum StandardCompoundsEnum {

    Alanine("Alanine, 2TMS", 116, 971, 3938),
    Alanine2("Alanine, TMS", 116, 971, 3938),
    StearicAcid("Stearic acid, TMS", 117, 875, 7258),
    ArachidonicAcid("Arachidonic acid, TMS", 117, 381, 12139),
    Cholesterol("Cholesterol, TMS", 129, 707, 13465),
    Proline("Proline, 2TMS", 142, 921, 3557),
    Proline2("Proline, TMS", 142, 921, 3557),
    Ornitine("Ornithine, 4TMS", 142, 549, 3513),
    Valine("Valine, 2TMS", 144, 603, 3510),
    Valine2("Valine, 2TMS", 152, 581, 3591),
    Valine3("Valine, TMS", 144, 603, 3510),
    Valine4("Valine, TMS", 152, 581, 3591),
    Lysine("Lysine, 4TMS", 156, 301, 3395),
    Lysine2("Lysine, TMS", 156, 301, 3395),
    Leucine("Leucine, 2TMS", 158, 792, 3983),
    Leucine2("Leucine, TMS", 158, 792, 3983),
    Isoleucine("Isoleucine, 2TMS", 158, 610, 3629),
    Isoleucine2("Isoleucine, TMS", 158, 610, 3629),
    Glycine("Glycine, 3TMS", 174, 523, 4069),
    Glycine2("Glycine, TMS", 174, 523, 4069),
    Methionine("Methionine, 2TMS", 176, 490, 5466),
    Methionine2("Methionine, TMS", 176, 490, 5466),
    Serine("Serine, 3TMS", 204, 267, 3230),
    Serine2("Serine, TMS", 204, 267, 3230),
    Phenylalanine("Phenylalanine, 2TMS", 218, 250, 3301),
    Phenylalanine2("Phenylalanine, TMS", 218, 250, 3301),
    Tyrosine("Tyrosine, 3TMS", 218, 399, 2724),
    Tyrosine2("Tyrosine, TMS", 218, 399, 2724),
    Threonine("Threonine, 3TMS", 219, 99, 3347),
    Threonine2("Threonine, TMS", 219, 99, 3347),
    AsparticAcid("Aspartic acid, 3TMS", 232, 220, 2935),
    AsparticAcid2("Aspartic acid, TMS", 232, 220, 2935),
    Hydroxybutyric("3-Hydroxybutyric acid, 2TMS", 233, 61, 5630),
    Hydroxybutyric2("3-Hydroxybutyric acid, TMS", 233, 61, 5630),
    GlutamicAcid("Glutamic acid, 3TMS", 246, 233, 3871),
    GlutamicAcid2("Glutamic acid, TMS", 246, 233, 3871),
    PalmiticAcid("Palmitic acid, TMS", 313, 109, 6742),
    PalmiticAcid2("Palmitic acid, TMS", 316, 113, 7222),
    Heptadecanoid("Heptadecanoic acid, TMS", 327, 105, 7091),
    LinoleicAcid("Linoleic acid, TMS", 337, 45, 9328),
    OleicAcid("Oleic acid, TMS", 339, 69, 8685),
    Dibromooctafluoro("4,4'-Dibromooctafluorobiphenyl", 456, 654, 13142),
    Glutamine("Glutamine, TMS", 156, 537, 4066),
    LacticAcid("Lactic acid, 2TMS", 191, 154, 4749),
    LacticAcid2("Lactic acid, TMS", 191, 154, 4749);
    private final String name;
    private final int intensity, sumIntensities, mass;

    StandardCompoundsEnum(String name, int mass, int intensity, int sumIntensities) {
        this.name = name;
        this.intensity = intensity;
        this.sumIntensities = sumIntensities;
        this.mass = mass;
    }

    public String getName() {
        return this.name;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public int getSumIntensity() {
        return this.sumIntensities;
    }

    public int getMass() {
        return this.mass;
    }
}
