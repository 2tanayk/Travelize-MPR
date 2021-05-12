//const functions = require("firebase-functions");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');
// The Firebase Admin SDK to access Firestore.
const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore()

// Take the text parameter passed to this HTTP endpoint and insert it into
// Firestore under the path /messages/:documentId/original

// exports.addMessage = functions.https.onRequest(async(req, res) => {
//     // Grab the text parameter.
//     const original = req.query.text;
//     // Push the new message into Firestore using the Firebase Admin SDK.
//     const writeResult = await admin.firestore().collection('messages').add({ original: original });
//     // Send back a message that we've successfully written the message
//     res.json({ result: `Message with ID: ${writeResult.id} added.` });
// });

// Listens for new messages added to /messages/:documentId/original and creates an
// uppercase version of the message to /messages/:documentId/uppercase

// exports.makeUppercase = functions.firestore.document('/messages/{documentId}')
//     .onCreate((snap, context) => {
//         // Grab the current value of what was written to Firestore.
//         const original = snap.data().original;

//         // Access the parameter `{documentId}` with `context.params`
//         functions.logger.log('Uppercasing', context.params.documentId, original);

//         const uppercase = original.toUpperCase();

//         // You must return a Promise when performing asynchronous tasks inside a Functions such as
//         // writing to Firestore.
//         // Setting an 'uppercase' field in Firestore document returns a Promise.
//         return snap.ref.set({ uppercase }, { merge: true });
//     });

//http://localhost:5001/finsmart-1c2dd/us-central1/addMessage?text=uppercaseme

exports.addToChatGroup = functions.region('asia-east2').firestore
    .document('Places/{placen}/{users}/{documentId}')
    .onCreate(async(snap, context) => {
        const data = snap.data()

        const createdDocId = snap.id
        const passionArray = data.passions
        const parameters = context.params
        const placesCollectionPath = `Places/${parameters.placen}/${parameters.users}`
        let maxIntersectionSize = 0
        let bestDocumentPath = ''
        let bestIntersection = []
        let bestMembersArray = []

        console.log('passionsArray', passionArray)
        console.log('placesCollectionPath', placesCollectionPath)
        console.log('created doc id', createdDocId)

        const res = await db.runTransaction(async transaction => {
                const chatsCollectionRef = db.collection(`Chats/${parameters.placen}/chats`)
                const placeDocumentRef = db.doc(`Places/${parameters.placen}`)
                let snapshots = []
                let placeName = ""
                let placeImgUrl = ""
                await transaction.get(chatsCollectionRef)
                    .then(function(value) {
                            console.log('value', value.docs)
                            snapshots = value.docs
                        },

                        function(error) {
                            console.log('error', error)
                        }
                    )

                await transaction.get(placeDocumentRef).then(function(value) {
                        docSnap = value.data()
                        placeName = docSnap.name
                        placeImgUrl = docSnap.url
                    },
                    function(error) {
                        console.log('error', error)
                    })


                //console.log('snapshot size', snapshots.size)
                if (snapshots.length === 0) {
                    const newChatDocumentRef = chatsCollectionRef.doc();
                    transaction.set(newChatDocumentRef, { passionIntersection: passionArray, members: [createdDocId], name: placeName, url: placeImgUrl })
                    return Promise.resolve(`new chat doc created,path- ${newChatDocumentRef.path}`)
                } else {
                    console.log('Retrieved snapshots size', snapshots.length)
                    snapshots.forEach(snapshot => {
                        const tempData = snapshot.data()
                        console.log(tempData.passionIntersection)
                        let tempIntersection = passionArray.filter(e => tempData.passionIntersection.includes(e))
                        console.log('intersection is', tempIntersection)
                        let tempIntersectionSize = tempIntersection.length
                        let tempDocumentPath = snapshot.ref.path
                        let tempMembersArray = tempData.members

                        if (tempIntersectionSize > maxIntersectionSize) {
                            maxIntersectionSize = tempIntersectionSize
                            bestDocumentPath = tempDocumentPath
                            bestIntersection = tempIntersection
                            bestMembersArray = tempMembersArray
                        }
                    })

                    if (maxIntersectionSize == 0) {
                        const newChatDocumentRef = chatsCollectionRef.doc();
                        transaction.set(newChatDocumentRef, { passionIntersection: passionArray, members: [createdDocId], name: placeName, url: placeImgUrl })
                        return Promise.resolve(`no match found,new chat doc created,path- ${newChatDocumentRef}`)
                    } else {
                        const existingChatDocumentRef = db.doc(bestDocumentPath)
                        bestMembersArray.push(createdDocId)
                        transaction.set(existingChatDocumentRef, { passionIntersection: bestIntersection, members: bestMembersArray, name: placeName, url: placeImgUrl })
                        return Promise.resolve(`match found,path- ${existingChatDocumentRef.path}`)
                    }
                }
            }).then(result => {
                console.log(result)
            })
            .catch(err => {
                console.error('Transaction has failed', err)
                return null
            })
        return res
    });