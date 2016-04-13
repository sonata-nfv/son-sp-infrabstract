/**
 * @author Dario Valocchi (Ph.D.)
 * @mail d.valocchi@ucl.ac.uk
 * 
 *       Copyright 2016 [Dario Valocchi]
 * 
 *       Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *       except in compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *       Unless required by applicable law or agreed to in writing, software distributed under the
 *       License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *       either express or implied. See the License for the specific language governing permissions
 *       and limitations under the License.
 * 
 */

package sonata.kernel.adaptor.commons.vnfd;


public interface Unit {

  public enum BandwidthUnit implements Unit {
    bps, kbps, Mbps, Gbps, Tbps;
  }

  public enum MemoryUnit implements Unit {
    B, kB, KiB, MB, MiB, GB, GiB, TB, TiB, PB, PiB;

    /**
     * Utility method to retrieve the multiplicative factor associated with this Memory Unit.
     * Warning: some factors are out of the long capabilities to represents them.
     * 
     * @return a long integer with the multiplicative factor.
     */
    public long getMultiplier() {
      if (this.equals(kB)) {
        return (long) Math.pow(10, 3);
      } else if (this.equals(MB)) {
        return (long) Math.pow(10, 6);
      } else if (this.equals(GB)) {
        return (long) Math.pow(10, 9);
      } else if (this.equals(TB)) {
        return (long) Math.pow(10, 12);
      } else if (this.equals(PB)) {
        return (long) Math.pow(10, 15);
      } else if (this.equals(KiB)) {
        return (long) Math.pow(2, 10);
      } else if (this.equals(MiB)) {
        return (long) Math.pow(2, 20);
      } else if (this.equals(GiB)) {
        return (long) Math.pow(2, 30);
      } else if (this.equals(TiB)) {
        return (long) Math.pow(2, 40);
      } else if (this.equals(PiB)) {
        return (long) Math.pow(2, 50);
      } else {
        return 1;
      }
    }
  }

  public enum FrequencyUnit implements Unit {
    Hz, kHz, MHz, GHz, THz;
  }

  public enum GeneralUnit implements Unit {
    percentage;
  }
}
