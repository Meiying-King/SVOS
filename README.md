## Skills Stack:
  ##### Java 17 + Spring Framwork 6. + Spring Framework 6.2.7 + Spring Boot 3.3.3 + Mysql + MyBatis + Vue3 + Element Plus + Redis + AI Model
## Project Description:
  ##### With the popularization of new energy vehicles in China, the company provides new energy vehicle leasing services. The platform realizes enterprise-level scheduling and management of vehicle information and user vehicle usage, monitors real-time vehicle data, and achieves effective vehicle utilization and cost control.
## Project Modules:
  ##### The project consists of seven core modules: User Module, Vehicle Module, Electronic Fence Module, Vehicle Application Module, Approval Module, Dictionary Module, and Dictionary Item Module.
## Personal Responsibilities:
  ##### 1. Vehicle Module：Managed vehicle status and information, including real-time data updates and status tracking.
  ##### 2. Electronic Fence Module：Designed the electronic fence functionality, and managed vehicle trajectory monitoring and alarm information.
  ##### 3. Vehicle Application Module：.Implemented the internal vehicle allocation process, including employee application submission. Designed a two-level approval workflow (with single approver fallback for lower-level leaders) and displayed application status to requesters.
  ##### 4. Approval Module：Developed the approval workflow for leaders to approve or reject applications from subordinates, and implemented system logic to update approval status based on reviewers'decisions.
  ##### 5. Electronic Fence Module：Designed the electronic fence functionality, and managed vehicle trajectory monitoring and alarm information.
  ##### 6. Dictionary Module：Designed the dictionary module, which manages system-wide dictionary data, including vehicle types, fuel types, and other common data.
  ##### 7. Adopted Redis to store vehicle and dictionary information for high-speed data query.
  ##### 8. Intelligent Customer Service Module: Built intelligent dialogue capabilities based on the LangChat large model combined with RAG retrieval-augmented generation technology, realizing intelligent Q&A services and effectively improving user consultation response efficiency and overall service experience.
## See the figure below for details:

1. #### Electronic Fence:

   ![img_1](..\svos\image\img_1.png)

2. #### intelligent customer service dialogue

![img](..\svos\image\img.png)	

# Configuration as follows

##### Please create a new application-local.yml file under ivos/src/main/resources.

​	application-local.yml

```
langchain4j:
  community:
    dashscope:
      chat-model:
        model-name: qwen-max
        api-key: <YouAPIKeyhere>
      streaming-chat-model:
        model-name: qwen-max
        api-key: <YouAPIKeyhere>
      embedding-model:
        model-name: text-embedding-v4
        api-key: <YouAPIKeyhere>
```



  #### 



​    
