
# 🐆 Cheetah Reliable Operation for Splitting & Stitching (CROSS)

`CheetahCutOrStitch` is a powerful and fast **Spring Boot CLI** application designed to:

- **Split (Cut)** large files into smaller, configurable-sized parts.
- **Stitch (Rebuild)** previously split parts into a single, original file.
- **Store metadata** in **MongoDB** for reliable and traceable file operations.

---

## 📦 **Key Features**

✅ **Cut (Split)**  
- Splits large files into smaller parts.  
- Parts are named using a dynamic padding system and include the hash of the original file.  
- Metadata (file parts, sizes, hash) are saved to MongoDB for traceability.

✅ **Stitch (Rebuild)**  
- Automatically rebuilds a file from its parts.  
- Verifies that the parts' count and size match the original file.  
- Ensures that the final file's size matches the original one.

✅ **Smart File Management**  
- Automatically verifies the number and size of file parts.  
- Clear and detailed log messages for each process step.

---

## 🚀 **Requirements**
- **Java 17+**  
- **Maven 3.8+**  
- **MongoDB** (local or cloud, like MongoDB Atlas)

---

## ⚙️ **Project Setup**

### 1. **Clone the Repository**
```bash
git clone <your-repo-url>
cd cheetahcutorstitch
```

---

### 2. **Configure MongoDB**
Ensure MongoDB is running and update the `application.properties` file:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/ccos
spring.data.mongodb.database=ccos
logging.level.root=INFO
```

> ⚡ **Note**: If using **MongoDB Atlas**, replace the URI with your cluster's connection string.

---

### 3. **Build the Project**
```bash
mvn clean install
```

---

## ⚡ **How to Use**

The application accepts commands via the command line.

---

### ✅ **1. Split a File (Cut)**

```bash
java -jar cheetahcutorstitch.jar --operation=cut --input=/path/to/largefile.txt --size=50MB --output=/path/to/output
```

- `--operation`: Specify the operation (`cut` or `stitch`).  
- `--input`: Path to the file to be split.  
- `--size`: Maximum size of each part (`KB` or `MB`).  
- `--output`: Destination directory for the split files.

> **Example**:  
```bash
java -jar cheetahcutorstitch.jar --operation=cut --input=/Users/edoardo/largefile.txt --size=50MB --output=/Users/edoardo/output
```

---

### ✅ **2. Stitch a File**

```bash
java -jar cheetahcutorstitch.jar --operation=stitch --input=/path/to/parts --output=/path/to/final
```

- `--input`: Directory containing the file parts.  
- `--output`: Directory where the final stitched file will be saved.

> **Example**:  
```bash
java -jar cheetahcutorstitch.jar --operation=stitch --input=/Users/edoardo/output --output=/Users/edoardo/final
```

---

### ✅ **File Naming Format**
Split files follow the naming convention:

```
001_abcd1234.part
002_abcd1234.part
...
```

- The number is dynamically padded based on the total number of parts.  
- The hash (`abcd1234`) is generated from the original file name.

---

## 📚 **MongoDB Metadata Structure**

For each split operation, a document is saved in MongoDB with the following structure:

```json
{
  "_id": "auto-generated-id",
  "hash": "abcd1234",
  "originalFileName": "largefile.txt",
  "originalFileSize": 1048576000,
  "parts": [
    { "name": "001_abcd1234.part", "size": 52428800 },
    { "name": "002_abcd1234.part", "size": 52428800 },
    { "name": "003_abcd1234.part", "size": 1048576 }
  ]
}
```

---

## ✅ **Built-In Verifications**
- ✔️ Verifies that the **number of parts** matches the metadata.  
- ✔️ Ensures that **each part's size** is correct.  
- ✔️ Confirms that the **sum of parts' sizes** equals the original file size.  
- ✔️ Checks that the **final stitched file's size** matches the original.

---

## ❌ **Error Handling**
- If the parts' sizes don't match, the process fails.  
- If the number of parts is incorrect, the process fails.  
- If the final file's size doesn't match the original, an exception is thrown.  
- All error messages are detailed and informative.

---

## 🔄 **Clean the Project**
```bash
mvn clean
```

---

## 🛠️ **Potential Future Enhancements**
- ✅ Add integrity check using **content hash**.  
- ✅ Implement a command to display saved metadata from MongoDB.  
- ✅ Support compression formats during the split process.  
- ✅ Generate detailed reports in JSON or CSV format.

---

## ❤️ **Contributing**
Feel free to open issues, submit pull requests, or suggest new features!

---

## 📄 **License**
This project is released under the [MIT](LICENSE) license.

---

## 👨‍💻 **Author**
Created with passion by [Your Name].

---

### 🚀 **Now Go Split and Stitch Like a Pro!**  
If you encounter any issues or have suggestions, don't hesitate to reach out! 😎
# 72b17cc36574d9f10d5551ce4607649dae5affe8683667d01fc1ec4e1f935576
# b9be2c463801b6e55ce0d9eb23b83056375eff75c78e7037021050ac48f36cff.part
