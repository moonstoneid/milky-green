import { ethers } from 'ethers';
import { SiweMessage } from 'siwe';
declare var window: any

const BACKEND_ADDR = "http://localhost:3000";
const domain = window.location.host;
const origin = window.location.origin;
const ethereum = window.ethereum;

var provider;
var signer:ethers.providers.JsonRpcSigner;

/**
 * Creates a EIP-4361 compatible message.
 * 
 * @param address The user's ethereum account address
 * @param statement Call-to-action string that is displayed in MetaMask
 * @returns EIP-4361 compatible message
 * 
 */
async function createSiweMessage(address:string, statement:string) {
   /* const res = await fetch(`${BACKEND_ADDR}/nonce`, {
        credentials: 'include',
    }); */
    const message = new SiweMessage({
        domain,
        address,
        statement,
        uri: origin,
        version: '1',
        chainId: '1',
        nonce: 'abc' //await res.text()
    });
    return message.prepareMessage();
}

/**
 * Creates a EIP-4361 compatible message and asks the user to sign the message with his private key.
 * The signed message is then sent to the backend for verification.
 * 
 */
async function signInWithEthereum() {
    // Create and sign message
    let message = await createSiweMessage(
        await signer.getAddress(),
        'Sign in with Ethereum to the app.'
    );
    const signature = await signer.signMessage(message);
 
    // Send signed message to backend for verification
    const res = await fetch(`${BACKEND_ADDR}/verify`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ message, signature }),
        credentials: 'include'
    });
    console.log(await res.text());
}

/**
 * Checks if MetaMask is installed. 
 * If true, it requests access to the MetaMask account.
 * 
 */
function connectWallet() {
    if (!ethereum) {
        let div = document.getElementById('errorDiv');
        div.innerHTML = 'You need to install MetaMask.'
        div.style.visibility ='visible';

        let loginButton = document.getElementById('loginButton');
        loginButton.style.visibility = 'hidden';

        return;    
    }
    
    // Connect wallet
    provider = new ethers.providers.Web3Provider(ethereum);
    signer = provider.getSigner();
    provider.send('eth_requestAccounts', []).catch(() => console.log('user rejected request'));
}

// Register events
document.getElementById ('loginButton').addEventListener ("click", signInWithEthereum, false);

// Try to connect wallet on page load
connectWallet();