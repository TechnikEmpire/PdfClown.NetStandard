PDF Clown for Java
PDF Clown Project [../main/README.html] > 


Project version: 0.1.2 - README revision: 0 (2013-02-04)

---------------
Introduction
---------------
This is the Java source code distribution of  [http://www.pdfclown.org/], a general-purpose library for the manipulation of PDF files.


---------------
Copyright
---------------
Copyright © 2006-2013 Stefano Chizzolini

Contacts:
 * url: http://www.stefanochizzolini.it [http://www.stefanochizzolini.it]


---------------
License
---------------
This program is free software; you can redistribute it and/or modify it under the terms of version 3 of the GNU Lesser General Public License as published by the Free Software Foundation.

References:
 * LGPL (GNU Lesser General Public License) version 3:
  * sources:
   * url: ../main/licenses/gnu.org/lgpl.html [../main/licenses/gnu.org/lgpl.html]
   * url: http://www.gnu.org/licenses/lgpl.html [http://www.gnu.org/licenses/lgpl.html]
   * mail: Free Software Foundation, Inc., 51 Franklin St - Fifth Floor, Boston, MA 02110-1301 USA.
  * restrictions: none
  * extensions: none


---------------
Disclaimer
---------------
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

IN NO EVENT SHALL THE COPYRIGHT HOLDER AND CONTRIBUTORS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE, EVEN IF THE COPYRIGHT HOLDER AND CONTRIBUTORS HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE SOFTWARE PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE COPYRIGHT HOLDER AND CONTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE COPYRIGHT HOLDER AND CONTRIBUTORS MAKE NO REPRESENTATIONS AND EXTEND NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE SOFTWARE WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.


---------------
Dependencies
---------------
PDF Clown for Java currently depends on:

 * Java 6 platform [http://java.sun.com]

---------------
Getting started
---------------
---------------
Eclipse IDE
---------------
The code projects of PDF Clown for Java have been configured to run for debugging under the Eclipse IDE [http://eclipse.org/]. They have been tested under Helios (3.6) on both GNU/Linux (Ubuntu [http://www.ubuntu.com]) 2.6.32+ and MS Windows NT 5.1+ systems.

In order to work under the Eclipse IDE, you have simply to follow these steps:

* launch Eclipse;
* select the workspace: on "Select a workspace" dialog, browse to "java" subdirectory and click "OK";
* import the projects:
            
* on workbench window, select "File" > "Import..." menu to open the "Import" wizard;
* in the "Select" page, select the import source "General" > "Existing Projects into Workspace", then click "Next";
* in the "Import Projects" page, browse for "Select root directory" and confirm "java" subdirectory (it should be proposed as default by the folder browser dialog). Now all the available projects should have appeared (and been checked) in the "Projects" list;
* click "Finish";
* verify Eclipse configuration:
            
 * text editor encoding MUST be set to "UTF-8" in order to properly deal with Unicode [http://unicode.org/standard/principles.html] text: open the "Preferences" window (select "Window" > "Preferences" menu), select "General" > "Workspace", then choose "UTF-8" ("Other" option) for "Text file encoding".
When the import completes, all the projects will be available in your workbench (switch to the "Java" perspective to see them represented within the "Project Explorer" view). The following resources have been configured to ease your compiling and debugging activities:

 * Ant build files: each project features an Ant build file that has already been integrated into Eclipse's automatic building workflow;
 * launchers: each sample project (specifically: pdfclown.samples.cli, pdfclown.samples.gui) features also a ready-to-use launcher you can run for debugging this way:
            
 * open the "Debug Configurations" window: right-click on a sample project's node (e.g. "pdfclown.samples.cli") in "Project Explorer" view, then select the contextual menu "Debug As" > "Debug Configurations...";
 * select a sample project's launcher: select "Java Application" > one of the available sample project launchers (e.g. "PDF Clown CLI Samples");
 * run the selected launcher: click "Debug".


---------------
Resources
---------------
 *  [pdfclown.lib/README.html]: PDF Clown source code
 *  [pdfclown.samples.cli/README.html]: Functionality demonstrations based on console interface
 *  [pdfclown.samples.gui/README.html]: Functionality demonstrations based on graphical user interface
 *  [pdfclown.samples.web/README.html]: Functionality demonstrations based on servlets
 *  [../main/doc/README.html]: PDF Clown common guides
 *  [../main/res/README.html]: Material supporting PDF Clown distribution
 *  [CHANGELOG.html]: Change chronology of PDF Clown for Java
 * PDF Clown home page [http://www.pdfclown.org]: Project home page
 * Navigation:
  * Current directory [.]: browse current section contents
  * Parent section [../main/README.html]: move to parent section
  * Previous section [../main/README.html]: move to previous section
  * Next section [pdfclown.lib/README.html]: move to next section
  * INDEX [../main/INDEX.html]: move to the distribution map
