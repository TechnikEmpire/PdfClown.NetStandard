PDF Clown Project



Project version: 0.1.2 - README revision: 0 (2013-02-04)

---------------
Introduction
---------------
This is the source code distribution of  [http://www.pdfclown.org/], a general-purpose library for the manipulation of PDF files implemented in multiple platforms (Java [../java/README.html], .NET [../dotNET/README.html]).


---------------
What's new?
---------------
This release [https://pdfclown.wordpress.com/2011/12/09/waiting-for-pdf-clown-0-1-2-release/] enhances several base structures, providing fully automated object change tracking and object cloning (allowing, for example, to copy page annotations and Acroform fields). It adds support to video embedding, article threads, page labels and several other functionalities.

 * [add] Primitive object model enhancements: see objects namespace.
 * [add] Advanced object cloning and traversal: see objects namespace
 * [add] Article threads: see documents.Articles, documents.interaction.navigation.page namespace
 * [add] Page labels: see documents.interaction.navigation.page.PageLabel
 * [add] Content transparency: see documents.contents.BlendModeEnum, documents.contents.ExtGState
 * [add] Text line alignment and image inlining: see documents.contents.composition.BlockComposer
 * [add] Multimedia: see documents.interaction.annotations.Screen, documents.interchange.multimedia namespace
 * [add] File references: see files.FileIdentifier, documents.files namespace

---------------
Copyright
---------------
Copyright © 2006-2013 Stefano Chizzolini

Contacts:
 * url: http://www.stefanochizzolini.it [http://www.stefanochizzolini.it]


---------------
License
---------------
This program is free software [http://en.wikipedia.org/wiki/Free_software]; you can redistribute it and/or modify it under the terms of version 3 of the GNU Lesser General Public License as published by the Free Software Foundation.

References:
 * LGPL (GNU Lesser General Public License) version 3:
  * sources:
   * url: licenses/gnu.org/lgpl.html [licenses/gnu.org/lgpl.html]
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
Community
---------------
The Community page [http://www.pdfclown.org/community.html] guides you through the resources that you can use to ask questions, request new features, report bugs, discuss and submit code contributions and keep yourself up-to-date about the project's development.


---------------
Updates
---------------
The  [http://sourceforge.net/projects/clown/] is hosted by SourceForge.net and referenced by PDF Clown's official website [http://www.pdfclown.org/]: please AVOID downloading from any other repository if you want to be sure its updates can be trusted.

This distribution represents the result of a release cycle which tipically spans over several months: instead of waiting for the final release, you can keep your copy of the PDF Clown's code base up-to-date synchronizing it with the  [http://sourceforge.net/scm/?type=svn&group_id=176158]. You have just to choose the branch more appropriate for your needs:

 * Fix branch <https://clown.svn.sourceforge.net/svnroot/clown/branches/0.1.2-Fix [https://clown.svn.sourceforge.net/svnroot/clown/branches/0.1.1-Fix]>: corrective branch (bug fixes for existing functionalities);
 * Trunk <https://clown.svn.sourceforge.net/svnroot/clown/trunk [https://clown.svn.sourceforge.net/svnroot/clown/trunk]>: evolutionary branch (all the latest & greatest along with the same bug fixes of the above-mentioned Fix branch).

---------------
Support it!
---------------
Are you successfully using this software? Remember that behind it there are human beings who enjoyed donating some effort to craft a nice piece of code -- you can demonstrate your appreciation in several ways:

 * donate: even a little PayPal transfer [http://www.stefanochizzolini.it/en/projects/clown/#Donations] is welcome, just to cheer your success;
 * contribute: have you extended the library to cover new functionalities? have you written useful sample code or documentation you'd like to share? let us know!;
 * communicate: inform your colleagues and Web community about this project and promote the broader adoption of free software [http://en.wikipedia.org/wiki/Free_software].

---------------
Resources
---------------
 *  [../java/README.html]: PDF Clown implementation for Java
 *  [../dotNET/README.html]: PDF Clown implementation for .NET
 *  [licenses/README.html]: Licenses applied to the PDF Clown distribution
 *  [doc/README.html]: PDF Clown common guides
 *  [res/README.html]: Material supporting PDF Clown distribution
 *  [CREDITS.html]: Who's behind PDF Clown development
 *  [WHATSNEW.html]: New features of the PDF Clown Project
 *  [ISSUES.html]: Known issues
 *  [TODO.html]: TODO list of the PDF Clown Project
 *  [INDEX.html]: Distribution map
 * PDF Clown home page [http://www.pdfclown.org]: Project home page
 * Navigation:
  * Current directory [.]: browse current section contents
  * Next section [../java/README.html]: move to next section
  * INDEX [INDEX.html]: move to the distribution map
