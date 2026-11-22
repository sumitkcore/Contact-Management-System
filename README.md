# Contact Management System (CMS)
## Group 17 | Java OOP Mini Project | Course Code: CSE2003
### School of Computer Science & Engineering | B.Tech CSE

![Java](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing%20%2B%20Java2D-5382a1?style=for-the-badge&logo=java&logoColor=white)
![Database](https://img.shields.io/badge/Database-MySQL%208.0%20%7C%20Persistent%20Engine-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Build](https://img.shields.io/badge/Build-Batch%20%7C%20PowerShell%20%7C%20Maven-blue?style=for-the-badge)
![Testing](https://img.shields.io/badge/Tests-100%25%20Passing-success?style=for-the-badge)

---

## 📌 Executive Summary

The **Contact Management System (CMS)** is a desktop application engineered in **Java 21 LTS** for the **CSE2003 (Object Oriented Programming in Java)** mini-project evaluation.

The system is designed with a multi-tier layered architecture (**Presentation / UI Layer**, **Business Service Layer**, **Data Access Object Layer**, and **Domain Model Layer**), demonstrating the core principles of Object-Oriented Software Engineering, 6 design patterns, multithreaded background auto-save daemons, asynchronous file I/O, custom exception handling hierarchies, and dual persistence engines (**Local Persistent Object Serialization Engine** and **MySQL 8.0 JDBC**).

---

## 👥 Group 17 Information & Team Distribution

| Role / Specialization | Module Ownership | Deliverables |
| :--- | :--- | :--- |
| **Lead Software Architect** | Domain Models, Inheritance, Polymorphism | Abstract `Contact`, `PersonalContact`, `BusinessContact`, `EmergencyContact` |
| **Database Architect** | JDBC, SQL DDL Schema, Transactions | `DatabaseConnectionManager`, `MySQLContactDAOImpl`, `schema.sql` |
| **Backend & Concurrency Engineer** | Service Layer, Multithreading, File I/O | `ContactServiceImpl`, `AutoSaveManager`, `CsvHandler`, `VCardHandler`, `JsonHandler` |
| **UI/UX & Design Engineer** | Modern Swing Desktop UI, Java2D Graphics | `MainFrame`, `ContactDetailPanel`, `CategoryPieChartPanel`, `ModernTheme` |

---

## 🎯 CSE2003 OOPJ Syllabus Mapping

Every module of the university OOPJ syllabus is implemented across the project codebase:

| Syllabus Module & Topic | Project Implementation File(s) | Conceptual Demonstration |
| :--- | :--- | :--- |
| **1. Principles of OOP & Encapsulation** | [`Contact.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/model/Contact.java)<br>[`PhoneNumber.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/model/PhoneNumber.java)<br>[`Address.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/model/Address.java) | Private members, controlled getters/setters, defensive copying of collections and dates, Value Objects. |
| **2. Inheritance & Class Hierarchy** | [`PersonalContact.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/model/PersonalContact.java)<br>[`BusinessContact.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/model/BusinessContact.java)<br>[`EmergencyContact.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/model/EmergencyContact.java) | Base class inheritance (`extends Contact`), specialized subtype attributes (`relationship`, `company`, `bloodGroup`, `priority`). |
| **3. Polymorphism & Dynamic Dispatch** | [`Contact.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/model/Contact.java#L60-L90)<br>[`ContactListRenderer.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/ui/components/ContactListRenderer.java) | Overridden abstract methods (`getSummary()`, `getSpecificDetailsMap()`, `toVCard()`, `validate()`), runtime method resolution. |
| **4. Abstraction & Interfaces** | [`Validatable.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/validation/Validatable.java)<br>[`ContactDAO.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/dao/ContactDAO.java)<br>[`ContactSortingStrategy.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/pattern/strategy/ContactSortingStrategy.java) | Contract-driven development, total decoupling of business contracts from underlying storage or UI presentation. |
| **5. Custom Exception Hierarchy** | [`ContactManagementException.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/exception/ContactManagementException.java)<br>[`InvalidContactDataException.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/exception/InvalidContactDataException.java)<br>[`DuplicateContactException.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/exception/DuplicateContactException.java)<br>[`ContactNotFoundException.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/exception/ContactNotFoundException.java) | Checked vs unchecked exception hierarchy, exception chaining (`Throwable cause`), try-catch-finally, try-with-resources. |
| **6. Java Collections & Generics** | [`GenericDAO.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/dao/GenericDAO.java)<br>[`FileContactDAOImpl.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/dao/FileContactDAOImpl.java) | `GenericDAO<T, ID>`, `ConcurrentHashMap<String, Contact>`, `TreeSet<String>` (case-insensitive tags), `List<Contact>`, Streams & Lambdas. |
| **7. Multithreading & Concurrency** | [`AutoSaveManager.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/service/AutoSaveManager.java)<br>[`ImportExportDialog.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/ui/dialogs/ImportExportDialog.java) | Daemon thread `ScheduledExecutorService` (auto-saves dirty contacts every 45s), `ReentrantReadWriteLock` in DAO, `SwingWorker` for async I/O. |
| **8. File Handling & Serialization** | [`ObjectSerializationHandler.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/io/ObjectSerializationHandler.java)<br>[`CsvHandler.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/io/CsvHandler.java)<br>[`VCardHandler.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/io/VCardHandler.java)<br>[`JsonHandler.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/io/JsonHandler.java) | Binary object serialization (`ObjectOutputStream`/`ObjectInputStream`), RFC-4180 CSV parser, vCard 3.0 export/import, JSON, persistent audit logger. |
| **9. Database Connectivity (JDBC)** | [`DatabaseConnectionManager.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/dao/DatabaseConnectionManager.java)<br>[`MySQLContactDAOImpl.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/dao/MySQLContactDAOImpl.java)<br>[`schema.sql`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/resources/schema.sql) | JDBC Driver registration, PreparedStatements (SQL Injection protection), Transactions (`setAutoCommit(false)`, `commit()`, `rollback()`), Batch updates. |
| **10. Modern GUI & Event Handling** | [`MainFrame.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/ui/MainFrame.java)<br>[`ContactDetailPanel.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/ui/view/ContactDetailPanel.java)<br>[`ModernTheme.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/ui/ModernTheme.java) | Master-Detail split pane, live search debounce, custom Java2D anti-aliased components, Dark/Light mode toggle, Keyboard shortcuts. |

---

## 🏛️ System Architecture

```mermaid
flowchart TD
    subgraph UI ["Presentation Layer (Swing / Java2D)"]
        MF[MainFrame]
        CDP[ContactDetailPanel]
        CED[ContactEditorDialog]
        IED[ImportExportDialog]
        AD[AnalyticsDialog]
        CSD[CallSimulatorDialog]
        TH[ModernTheme (Dark / Light)]
    end

    subgraph ServiceLayer ["Service Layer (Business Logic)"]
        CS[ContactService / ContactServiceImpl]
        VAL[ContactValidator]
        ASM[AutoSaveManager (Background Daemon Thread)]
        EM[ContactEventManager (Observer Pattern)]
        AL[AuditLogger (File Logging)]
    end

    subgraph PatternLayer ["Design Patterns"]
        FAC[ContactFactory & DAOFactory]
        BLD[ContactBuilder]
        STR[Sorting & Export Strategy]
        OBS[ContactChangeListener]
    end

    subgraph DataLayer ["Data Access Layer (DAO)"]
        CDAO["<<interface>> ContactDAO"]
        FDAO[FileContactDAOImpl (ReentrantReadWriteLock)]
        MDAO[MySQLContactDAOImpl (JDBC PreparedStatements)]
        DCM[DatabaseConnectionManager (Singleton)]
    end

    subgraph Storage ["Physical Persistence Engines"]
        BIN[("contacts_data.dat (Binary Serialization)")]
        SQL[("MySQL Database (cms_contacts, cms_phone_numbers, etc.)")]
        CSV["contacts_export.csv (RFC-4180)"]
        VCF["contacts_export.vcf (vCard 3.0)"]
        JSON["contacts_export.json (JSON)"]
    end

    UI --> ServiceLayer
    ServiceLayer --> PatternLayer
    ServiceLayer --> DataLayer
    DataLayer --> Storage
    EM -.->|Notify on EDT| UI
    ASM -.->|Periodic Flush| DataLayer
```

---

## 💎 Design Patterns Implemented

1. **Singleton Pattern**:
   - [`DatabaseConnectionManager.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/dao/DatabaseConnectionManager.java): Provides single JDBC connection factory with fallback.
   - [`ContactEventManager.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/pattern/observer/ContactEventManager.java): Central event bus for UI change broadcasts.
   - [`AuditLogger.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/io/AuditLogger.java): Thread-safe file activity logger.
   - [`AutoSaveManager.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/service/AutoSaveManager.java): Singleton background scheduler.

2. **Data Access Object (DAO) Pattern**:
   - Decouples domain logic from storage mechanisms (`ContactDAO`, `FileContactDAOImpl`, `MySQLContactDAOImpl`).

3. **Factory Pattern**:
   - [`ContactFactory.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/pattern/factory/ContactFactory.java): Instantiates appropriate `PersonalContact`, `BusinessContact`, or `EmergencyContact`.
   - [`DAOFactory.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/dao/DAOFactory.java): Supplies MySQL DAO or auto-detects fallback to persistent file engine.

4. **Builder Pattern**:
   - [`ContactBuilder.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/pattern/builder/ContactBuilder.java): Fluent builder enabling readable chained construction of complex polymorphic contacts.

5. **Observer Pattern**:
   - [`ContactChangeListener.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/pattern/observer/ContactChangeListener.java) & [`ContactEventManager.java`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/java/com/cms/pattern/observer/ContactEventManager.java): Decoupled event notification updating sidebar counts, lists, and status bar automatically upon contact changes.

6. **Strategy Pattern**:
   - **Sorting Strategy**: Dynamic sorting algorithms (`NameAscendingStrategy`, `NameDescendingStrategy`, `DateAddedNewestStrategy`, `FavoritesFirstStrategy`, `CategoryStrategy`).
   - **Export Strategy**: Multi-format exports (`CsvExportStrategy`, `VCardExportStrategy`, `JsonExportStrategy`).

---

## 🚀 How to Build and Run the Application

### Option 1: 1-Click Launch (Windows Batch Script - Recommended)
Double click or execute in command prompt:
```bat
# From repository root:
.\run.bat
```
*(Automatically builds the project if not already built and launches the modern desktop application).*

### Option 2: PowerShell
```powershell
.\run.ps1
```

### Option 3: Manual Compilation & Execution via JDK 21
```powershell
cd java-fullstack
& "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.10002-hotspot\bin\javac.exe" -encoding UTF-8 -cp "lib\*" -d target\classes (Get-ChildItem -Path .\src\main\java -Filter *.java -Recurse | ForEach-Object { $_.FullName })
& "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.10002-hotspot\bin\java.exe" -cp "target\classes;lib\*" com.cms.Main
```

---

## 🧪 Comprehensive Unit Test Suite

The project includes an automated test runner with assertions enabled:
```bat
# From repository root:
.\test.bat
```

### Test Suite Execution Output:
```
=================================================
CONTACT MANAGEMENT SYSTEM — MASTER TEST SUITE
Group 17 | CSE2003 OOP Mini Project Verification
=================================================

Running ContactModelTest...
✓ ContactModelTest: ALL TESTS PASSED
Running ContactValidationTest...
✓ ContactValidationTest: ALL TESTS PASSED
Running ContactServiceTest...
✓ ContactServiceTest: ALL TESTS PASSED
Running SerializationAndIoTest...
✓ SerializationAndIoTest: ALL TESTS PASSED

-------------------------------------------------
TEST SUITE EXECUTION SUMMARY:
Passed: 4 / 4 Modules
Failed: 0 / 4 Modules
>>> ALL OOP & DATA INTEGRITY TESTS PASSED 100% <<<
=================================================
```

---

## 🗄️ Database Architecture & DDL Schema

When configured for MySQL, CMS uses the normalized database schema in [`schema.sql`](file:///c:/Users/Sumit%20Kumar/Downloads/java-fullstack/java-fullstack/src/main/resources/schema.sql):

- **`cms_contacts`**: Master contact table storing core identity, subtype data, favorite flag, and soft-delete state.
- **`cms_phone_numbers`**: 1-to-many relationship supporting multiple phone lines per contact (Mobile, Work, Home) with `CASCADE DELETE`.
- **`cms_contact_tags`**: Normalized tag index supporting instant multi-tag queries.
- **`cms_call_logs`**: Call interaction history with duration, call type, timestamp, and notes.
- **`cms_audit_logs`**: System audit log tracking all CREATE, UPDATE, DELETE, and EXPORT actions.

> **Zero-Config Portability**: If MySQL is not installed on the evaluation PC, CMS **automatically falls back** to the high-performance local file serialization engine (`contacts_data.dat`), pre-seeded with 35+ contacts, so the panel sees a running system with zero setup hurdles!

---

## 🎓 Viva Voce Questions & Answers (Evaluation Prep)

**Q1: How is Polymorphism demonstrated in this project?**
> *Answer:* The abstract class `Contact` defines abstract methods like `getSummary()`, `getSpecificDetailsMap()`, `toVCard()`, and `validate()`. At runtime, a polymorphic `List<Contact>` executes the specific subclass implementations for `PersonalContact`, `BusinessContact`, or `EmergencyContact` via dynamic method dispatch.

**Q2: What is the difference between Checked and Unchecked exceptions in your hierarchy?**
> *Answer:* `ContactManagementException`, `InvalidContactDataException`, and `DuplicateContactException` are **Checked Exceptions** extending `Exception`, requiring explicit compile-time handling when performing contact mutations. Conversely, `DatabaseException` and `StorageException` are **Unchecked Exceptions** extending `RuntimeException`, wrapping unexpected low-level SQL or I/O failures.

**Q3: How does the Observer Pattern improve UI responsiveness?**
> *Answer:* Through `ContactEventManager` and `ContactChangeListener`. Whenever a contact is added, modified, or deleted in the Service Layer, the event manager safely notifies all registered UI components on the Swing Event Dispatch Thread (`SwingUtilities.invokeLater`), eliminating tight coupling between backend services and UI forms.

**Q4: How did you ensure thread safety in the File Storage DAO?**
> *Answer:* `FileContactDAOImpl` uses `java.util.concurrent.locks.ReentrantReadWriteLock`. Multiple concurrent read operations (searches, queries) acquire the `readLock()` simultaneously, while mutation operations (add, update, delete, flush) acquire an exclusive `writeLock()`, preventing race conditions.

**Q5: How does the Auto-Save daemon work?**
> *Answer:* `AutoSaveManager` runs a `ScheduledExecutorService` with a daemon thread (`t.setDaemon(true)`). Every 45 seconds, it flushes uncommitted in-memory states to disk asynchronously without freezing the user interface.
