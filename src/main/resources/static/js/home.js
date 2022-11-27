const revokeConsentButton = document.getElementById('revoke-consent-button');

let checkedConsentsCnt = 0;
const consentsContainer = document.getElementById('consents-container');
consentsContainer && consentsContainer.addEventListener('click',
    function(e) {
        if (e.target.type === 'checkbox') {
            checkedConsentsCnt = calcNewCheckedCnt(e.target.checked, checkedConsentsCnt);
            enableRevokeConsentButton(checkedConsentsCnt > 0);
        }
    }, false);

function enableRevokeConsentButton(enable) {
    revokeConsentButton.disabled = !enable;
}

const deleteAuthorizationButton = document.getElementById('delete-authorization-button');

let checkedAuthorizationsCnt = 0;
const authorizationsContainer = document.getElementById('authorizations-container');
authorizationsContainer && authorizationsContainer.addEventListener('click',
    function(e) {
        if (e.target.type === 'checkbox') {
            checkedAuthorizationsCnt = calcNewCheckedCnt(e.target.checked, checkedAuthorizationsCnt);
            enableDeleteAuthorizationButton(checkedAuthorizationsCnt > 0);
        }
    }, false);

function enableDeleteAuthorizationButton(enable) {
    deleteAuthorizationButton.disabled = !enable;
}

function calcNewCheckedCnt(checked, checkedCnt) {
    return checked ? checkedCnt+1 : checkedCnt-1;
}