
import org.jutils.jhardware.HardwareInfo;
import org.jutils.jhardware.model.ProcessorInfo;

import java.io.*;
import java.lang.management.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
public class HTTPClient  extends  Thread{
    public static void main(String[] args) {

        int port = 8082;

        Runtime runtime = Runtime.getRuntime();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started!");

            while (true) {
                // ожидаем подключения
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");

                // для подключившегося клиента открываем потоки
                // чтения и записи
                try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                     PrintWriter output = new PrintWriter(socket.getOutputStream())) {

                    // ждем первой строки запроса
                    while (!input.ready()) ;

                    // считываем и печатаем все что было отправлено клиентом
                    System.out.println();
                    while (input.ready()) {
                        System.out.println(input.readLine());
                    }

                    // отправляем ответ
                    output.println("HTTP/1.1 200 OK");
                    output.println("Content-Type: text/html; charset=utf-8");
                    output.println();
                    output.println("<h1 align = 'center'><b>System Information!</b></h1>");

                    output.println("<p align = 'center'><b>OS information</b></p>");
                    output.println("<p><pre><i>OS</i>: " +System.getProperty("os.name")+" Version "+ System.getProperty("os.version")+"</pre></p>");
                    output.println("<p><pre><i>Arch</i>: " +System.getProperty("os.arch")+"</pre></p>");
                    output.println("<p><pre><i>Available processors (cores)</i>: " +runtime.availableProcessors()+"</pre></p>");

                    output.println("<p align = 'center'><b>Filesystem roots</b></p>");
                    File[] roots = File.listRoots();
                    for (File root : roots) {
                        output.println("<p><pre><b>\tFile system root: " + root.getAbsolutePath() + "</b></pre></p>");
                        output.println("<p><pre>Total space (GB): " + (double)root.getTotalSpace() /1073741824+ "</pre></p>");
                        output.println("<p><pre>Free space (GB): " + (double)root.getFreeSpace()/1073741824+ "</pre></p>");
                        output.println("<p><pre>Usable space (GB): " + (double)root.getUsableSpace() /1073741824+ "</pre></p>");
                    }

                    output.println("<p align = 'center'><b>Memory</b></p>");
                    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                    output.println("<p><pre>Initial memory: "+ (double)memoryMXBean.getHeapMemoryUsage().getInit() /1073741824 +" GB</pre></p>");
                    output.println("<p><pre>Used heap memory: "+ (double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824+" GB</pre></p>");
                    output.println("<p><pre>Max heap memory: "+ (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824+" GB</pre></p>");
                    output.println("<p><pre>Committed memory: "+ (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824 +" GB</pre></p>");
                    com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                    output.println("<p><pre>physical Memory Size: " + (double)os.getTotalPhysicalMemorySize()/1073741824+" GB</pre></p>");
                    output.println("<p><pre>free Physical Memory: " +(double)os.getFreePhysicalMemorySize()/1073741824+" GB</pre></p>");
                    output.println("<p><pre>free Swap Size: " + (double)os.getFreeSwapSpaceSize()/1073741824+" GB</pre></p>");
                    output.println("<p><pre>commited VirtualMemory Size: " + (double)os.getCommittedVirtualMemorySize()/1073741824+" GB</pre></p>");
                    output.println("<p><pre>TotalSwapSpaceSize: " + (double)os.getTotalSwapSpaceSize()/1073741824+" GB</pre></p>");

                    output.println("<p align = 'center'><b>Cpu</b></p>");
                    output.println("<p><b>General</b></p>");
                    ProcessorInfo pinf =  HardwareInfo.getProcessorInfo();
                    output.println("<p><pre><i>Family: </i>"+pinf.getFamily()+"</pre></p>");
                    output.println("<p><pre><i>Vendor id: </i>"+pinf.getVendorId()+"</pre></p>");
                    output.println("<p><pre><i>Model: </i>"+pinf.getModel()+"</pre></p>");
                    output.println("<p><pre><i>Model name: </i>"+pinf.getModelName()+"</pre></p>");
                    output.println("<p><pre><i>Cores: </i>"+pinf.getNumCores()+"</pre></p>");
                    output.println("<p><pre><i>Stepping: </i>"+pinf.getStepping()+"</pre></p>");
                    output.println("<p><pre><i>MHz: </i>"+pinf.getMhz()+"</pre></p>");
                    output.println("<p><pre><i>Process CPU load: </i>"+os.getProcessCpuLoad()+"</pre></p>");
                    output.println("<p><pre><i>Process CPU time </i>"+os.getProcessCpuTime()+"</pre></p>");
                    output.println("<p><pre><i>System CPU load </i>"+os.getSystemCpuLoad()+"</pre></p>");
                    output.println("<p><pre><i>System load avarage </i>"+os.getSystemLoadAverage()+"</pre></p>");

                    output.println("<p><b>Threads</b></p>");
                    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
                    for(Long threadID : threadMXBean.getAllThreadIds()) {
                        ThreadInfo info = threadMXBean.getThreadInfo(threadID);
                        output.println("<p><pre><b>Thread name--></b> " + info.getThreadName() + "\nState-->" + info.getThreadState() + "\nTime--> " + threadMXBean.getThreadCpuTime(threadID)+" ns</pre></p>");
                        output.println();
                    }
                    output.println("<p align = 'center'><b>Running procceses</b></p>");
                    try {
                        String line;
                        Process p = Runtime.getRuntime().exec
                                (System.getenv("windir") +"\\system32\\"+"tasklist.exe");
                        BufferedReader input1 =
                                new BufferedReader(new InputStreamReader(p.getInputStream()));
                        int counter = 0;
                        while ((line = input1.readLine()) != null) {
                            if(counter == 0) {
                                counter++;
                                continue;
                            }
                            if(counter < 2)
                                output.println("<p><pre>Name\t\t\t     PID    Session Name     № of session    Mem</pre></p>");
                            else{
                                if(counter ==2){
                                    output.println("<p><pre>"+ line+ "</pre></p>");
                                    counter++;
                                continue;}

                                String proc;
                                proc = line.substring(0, line.length() - 2);
                                proc+="Kb";
                                output.println("<p><pre>"+ proc+ "</pre></p>");}
                            counter++;
                        }
                        input1.close();
                    } catch (Exception err) {
                        err.printStackTrace();
                    }

                    output.flush();

                    // по окончанию выполнения блока try-with-resources потоки,
                    // а вместе с ними и соединение будут закрыты
                    System.out.println("Client disconnected!");
                    socket.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}