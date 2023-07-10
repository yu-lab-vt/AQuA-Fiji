# AQuA Fiji plugin

This is the site for the Fiji plugin of AQuA.
For details, please visit [the main repository](https://github.com/yu-lab-vt/AQuA).

If you just wish to download the plugin, click [here](https://github.com/yu-lab-vt/AQuA-Fiji/releases/download/v1.0/Aqua-1.0.jar), or check the [releases](https://github.com/yu-lab-vt/AQuA-Fiji/releases).

Please note, due to the resolution issue and the implementation (some MATLAB functions cannot be found in Java and implemented by authors), there could be slight differences between the results of Fiji version and MATLAB version.

# Updates

**7/10/2023:**

Synchronize the update of MATLAB AQuA version:
This update solves one issue not detected before. In some special cases, two connected distinct signals that have an obviously different rising time difference may be considered as one (super) event. This update is to solve it.

**07/04/2023:** 

This update fixes the error report during "running all steps".
This update also synchronizes the update of MATLAB AQuA version:
1) In the first step, we synchronize the update of MATLAB version that removes the randomness during calculating the intensity baseline. Now after removing F0, dF with a negative value won't be replaced by a random value.
2) In the second step -- finding super voxels, we synchronize the update of MATLAB version that allows the software to find events in the first frame and end frame. Besides, the super voxel detection now more depends on the "dF" rather than "data", which is the same as the MATLAB version.

**06/13/2023:** 

Fix one little bug about the error report during running step 3.

**03/24/2023:** 

This update also synchronizes the update of MATLAB AQuA version in the first step:
1) Using the estimated noise after smoothing the image to select active regions, rather than using the estimated noise before smoothing.
2) Modify the part of searching local maxima. Previous implementation is not consistent with MATLAB version. Now the code synchronizes the two versions.

**10/19/2019:** 

Repair the bug that 'minimum correlation' in merging step cannot be set to float data.
