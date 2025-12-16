# Chat Application

A simple Java-based console chat application that supports **multiple clients**, **broadcast messaging**, and **private messaging**. The system uses a server-client architecture where multiple clients connect to a central server to exchange messages in real time.

## 📌 What This Chat App Does

* Allows multiple users (clients) to join the chat room.
* Supports **broadcast messages** (everyone can see).
* Supports **private messages** using the command: `/w <username> <message>`.
* Displays all chat history in the client console.
* Server listens on port **12345**.

---

# 🚀 How to Run the Chat Application

## **Prerequisites**

Make sure you have Java Development Kit (JDK) installed.
You can verify by typing:

```
javac -version
```

---

## **Step 1: Compile the Code**

Open your terminal or command prompt, navigate to the folder containing the files, then run:

```
javac ChatServer.java ChatClient.java
```

This will generate the compiled files:

* `ChatServer.class`
* `ChatClient.class`

---

## **Step 2: Run the Server**

In the same terminal, start the server:

```
java ChatServer
```

You should see:

```
The chat server is running on port 12345
```

---

## **Step 3: Run Client A (Example: Alice)**

Open a **new** terminal window, navigate to the same folder, and run:

```
java ChatClient
```

Enter a username when asked — for example:

```
Alice
```

---

## **Step 4: Run Client B (Example: Bob)**

Open a **third** terminal window, navigate to the same folder, and run:

```
java ChatClient
```

Enter:

```
Bob
```

---

## **Step 5: Test the Features**

### **Broadcast Message**

In Alice's terminal:

```
Hello everyone
```

Bob should receive this.

### **Private Message**

In Bob's terminal:

```
/w Alice Hi secret message
```

Only Alice should see this.

---
