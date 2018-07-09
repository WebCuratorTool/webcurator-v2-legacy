<form method="post" action="curator/target/${urlPrefix}-basic-credentials.html">
<input type="hidden" name="listIndex" value="${command.listIndex}"/>
<input type="hidden" id="actionCmd" name="actionCmd" value="save"/>
<span class="subBoxTitle">Profile &raquo; Overrides &raquo; Basic Credentials</span>
<table cellpadding="3" cellspacing="0" border="0">
  <tr>
    <td class="subBoxTextHdr">Domain</td>
    <td class="subBoxText"><input name="credentialsDomain" value="${command.credentialsDomain}"></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Realm</td>
    <td class="subBoxText"><input name="realm" value="${command.realm}"></td>
  </tr>
  <tr>
    <td class="subBoxTextHdr">Username</td>
    <td class="subBoxText"><input name="username" value="${command.username}"></td>
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