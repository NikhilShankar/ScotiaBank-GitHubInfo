#### ScotiaBank - GithubInfo Repo Documentation

### Main Screen

- Enter a github user id
- Search button
- Displays user avatar and user name folowed by a list of repos
- If the user has more than 100 repos the network layer calls the paginated api until 
  all repositories are fetched. 
- A technical decision was taken to not implement pagination since for most users the number of repos will
  will be less than 100 and also due to another constraint of finding the total forks across all repositories to assign 
  a star badge to the user in the detail screen and to display the total forks.

### Design Anomalies to be addressed

- The design closely resembles Material2 components and the project uses Material3 library and some of the default UI appearance 
might seem a bit different eg. The space between the label and the bottom line in text field is a bit more than the screenshot shared in the assignment pdf.


### Testing



### Localization

- In order to support localization care have been given to not add any hardcoded strings inside code. Instead
all strings are defined and referred from strings.xml

### Multipane Support

- A technical decision to focus on phone screens was taken even though a list-detail approach for supporting devices 
 with expanded width or foldable screens was initially thought of. This can be done in future by making use of WindowSize class 
 and by using compose list - detail component. (ListDetailPaneScaffold)