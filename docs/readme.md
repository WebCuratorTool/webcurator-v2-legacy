# WCT Documentation
### https://webcuratortool.readthedocs.io/en/latest/
WCT documentation is written using the ReStructuredText format and hosted on readthedocs.org.

## Read the Docs (https://readthedocs.org/)

Read the Docs simplifies software documentation by automating building, versioning, and hosting of your docs for you. 
Think of it as Continuous Documentation.

Never out of sync
- Whenever you push code to your favorite version control system, whether that is Git, Mercurial, 
Bazaar, or Subversion, Read the Docs will automatically build your docs so your code and 
documentation are always up-to-date.

Multiple versions
- Read the Docs can host and build multiple versions of your docs so having a 1.0 version of 
your docs and a 2.0 version of your docs is as easy as having a separate branch or tag in your 
version control system.

Free and open source
- Read the Docs is free and open source and hosts documentation for nearly 100,000 large and 
small open source projects in almost every human and computer language.

## ReStructuredText

reStructuredText is an easy-to-read, what-you-see-is-what-you-get plaintext markup syntax and parser system. 
It is useful for in-line program documentation (such as Python docstrings), for quickly creating simple web pages, 
and for standalone documents. reStructuredText is designed for extensibility for specific application domains. The 
reStructuredText parser is a component of Docutils. reStructuredText is a revision and reinterpretation of the 
StructuredText and Setext lightweight markup systems.

#### Links

- http://docutils.sourceforge.net/rst.html
- http://docutils.sourceforge.net/docs/user/rst/quickstart.html
- http://docutils.sourceforge.net/docs/ref/rst/directives.html
- http://docutils.sourceforge.net/docs/user/rst/quickref.html


## Structure

The following is the structure committed to the WCT git project. There are 
additional folders/files generated in building and testing the documentation
that should not be committed to the repository.
  
- _static/
    - developer-guide/
    - system-administrator-guide/
    - user-manual/
    - theme_overrides.css
- guides/
    - developer-guide.rst
    - system-administrator-guide.rst
    - user-manual.rst
- conf.py
- index.rst
- make.bat
- Makefile

#### _static
The _static folder holds assets for rendering the documentation. This includes
all the images used in each guide.
There is also a stylesheet here used to override the default width of tables.

#### guides
The guides folder holds all the ReStructuredText (.rst) files, which is the 
documentation source

#### index.rst
index.rst is the landing page used on readthedocs. It contains links to the 
individual guides.

## Building and viewing the documentation locally

### Sphinx
Sphinx is a documentation generator written and used by the Python community. It is 
written in Python, and also used in other environments. Sphinx converts reStructuredText 
files into HTML websites and other formats including PDF, EPub, Texinfo and man.

#### Install Sphinx
http://www.sphinx-doc.org/en/master/

#### Install readthedocs theme
`pip install sphinx_rtd_theme`

#### Build
Using the make files located in the WCT docs folder, build the documentation.

- Linux: `make html`
- Windows: `make.bat html`

#### View
After building, there should be *_build* directory created. Locate and open 
*docs/_build/html/index.html* to view the documentation.

## Conversion from old documentation

The following steps were taken in converting the old documentation (prior to WCT v2.0), from 
Microsoft Word to the ReStructuredText format. These were based on the instructions found
here https://peintinger.com/?p=365.

- Open and save all Microsoft Word documents in the .docx format.
- Download and install pandoc (https://github.com/jgm/pandoc/releases/tag/1.16.0.2).
- Convert the .docx files to .rst `pandoc.exe -f docx input.docx -t rst -o output.rst`.
- Extract all images from the .docx files `unzip .\input.docx`.
- Move the Word media folder to the current working directory `mv .\word\media .`.
- Convert any non-png images to png, this was done use paint.
- Update the filenames for any converted images in their respective .rst file.
- Install Sphix, and run `sphinx-quickstart`
- Move converted .rst files and images into new Sphinx folder structure.
- Update image paths in .rst files to match new location.

