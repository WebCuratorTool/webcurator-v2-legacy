<form method="post" action="curator/target/${urlPrefix}-form-credentials.html">
<input type="hidden" name="listIndex" value="${command.listIndex}"/>
<input type="hidden" id="actionCmd" name="actionCmd" value="save"/>
<span class="subBoxTitle">Profile &raquo; Overrides &raquo; Form Credentials</span>
<table cellpadding="3" cellspacing="0" border="0">
  <tr>
    <td class="subBoxTextHdr">Domain</td>
    <td class="subBoxText"><input name="credentialsDomain" value="${command.credentialsDomain}"></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Login URI</td>
    <td class="subBoxText"><input name="loginUri" value="${command.loginUri}" size="60"></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">HTTP Method</td>
    <td class="subBoxText">
      <select name="httpMethod">
        <option value="GET" ${command.httpMethod == 'GET' ? 'selected' : ''}>GET</option>
        <option value="POST" ${command.httpMethod == 'POST' ? 'selected' : ''}>POST</option>
      </select>
  </tr>  
  <tr>
    <td class="subBoxTextHdr">Username Field</td>
    <td class="subBoxText"><input name="usernameField" value="${command.usernameField}"></td>
  </tr>      
  
  <tr>
    <td class="subBoxTextHdr">Username</td>
    <td class="subBoxText"><input name="username" value="${command.username}"></td>
  </tr>      

  <tr>
    <td class="subBoxTextHdr">Password Field</td>
    <td class="subBoxText"><input name="passwordField" value="${command.passwordField}"></td>
  </tr>      
  
  <tr>
    <td class="subBoxTextHdr">Password</td>
    <td class="subBoxText"><input name="password" value="${command.password}"></td>
  </tr>      
</table>
<br/>
<br/>
<input type="image" src="images/generic-btn-save.gif" alt="Save" title="Save" width="82" height="23" border="0" /><img src="images/x.gif" alt="" width="10" height="1" border="0" />
<input type="image" src="images/generic-btn-cancel.gif" alt="Cancel" title="Cancel" width="82" height="23" border="0" onclick="document.getElementById('actionCmd').value='cancel'"/>
</form>