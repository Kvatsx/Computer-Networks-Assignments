// Kaustav Vats ( 2016048 )

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/types.h>
#include "pthread.h"

#define PORT 5555
#define BUFSIZE 1024

pthread_t thread1, thread2;

void * SendMessage(void * arg) {

    int client_socket = (int) arg;
    char Buffer[BUFSIZE] = {0};

    printf("client_socket: %d\n", client_socket);
    while(1) {
        // printf("%s ", "Enter your mssg:");
        fgets(Buffer, BUFSIZE, stdin);
        // printf("Input taken\n");
        if (strncmp(Buffer, "exit", 4) == 0) {
            close(client_socket);
            pthread_exit(&thread2);
            return NULL;
        }
        if (send(client_socket, Buffer, strlen(Buffer), 0) == -1) {
            perror("send error\n");
        }
        // printf("Sent done\n");
    }
}

void * ReceiveMessage(void * arg) {

    int socket = (int) arg;
    while(1) {
        // sleep(1);
        char Buffer[BUFSIZE] = {0};

        int MssgRecvStatus;
        if ((MssgRecvStatus = recv(socket, Buffer, BUFSIZE, 0)) <= 0) {
            if (MssgRecvStatus == 0) {
                printf("Server closed!\n");
            }
            else {
                perror("recv error!\n");
            }
            close(socket);
            pthread_exit(&thread1);
            return NULL;
        }
        // printf("MssgRecvStatus: %d\n", MssgRecvStatus);
        // Buffer[strlen(Buffer) - 1] = '\0';
        printf("\n%s\n", Buffer);
    }
    return NULL;
}

int main(int argc, char const *argv[])
{
    int FileDescriptor_Socket;
    struct sockaddr_in Address;
    socklen_t AddressLength = sizeof(Address);
    char Buffer[BUFSIZE] = {0};
    int MssgRecvStatus;

    /* Creating a server File descriptor.
    IPv4
    SOCK_STREAM: TCP connection
    Protocol value: 0
    */
    if ((FileDescriptor_Socket = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        perror("Socket not created\n");
		exit(1);
    }

    Address.sin_family = AF_INET;
    Address.sin_port = htons(PORT);
    Address.sin_addr.s_addr = inet_addr("127.0.0.1");

    /*
    Converts IPv4 & IPv6 from text to Binary address.
    returns 1 on success.
    domain: AF_INET: IPv4
    src: "127.0.0.1"
    dest: &Address.sin_addr
    Conerts character string src into a network address structure,
    then copies the network address structure to dest.
    */
    // if ( inet_pton(AF_INET, "127.0.0.1", &Address.sin_addr) <= 0 ) {
    //     perror("Invalid Address!\n");
    //     exit(1);
    // }

    memset(Address.sin_zero, '\0', sizeof Address.sin_zero);

    /*
    Connects the socket referred by the FileDescriptor_Socket 
    to the address specified by "Address". 
    If connection successful then returns 0 else -1.
    */
    if ( connect(FileDescriptor_Socket, (struct sockaddr *)&Address, sizeof(struct sockaddr_in)) < 0 ) {
        perror("Connection Fail\n");
        exit(1);
    }
    printf("Please Enter your Name: ");
    fgets(Buffer, BUFSIZE, stdin);
    if (send(FileDescriptor_Socket, Buffer, strlen(Buffer), 0) == -1) {
        perror("send error\n");
    }

    pthread_create(&thread1, NULL, ReceiveMessage, (void *) FileDescriptor_Socket );
    pthread_create(&thread2, NULL, SendMessage, (void *) FileDescriptor_Socket);

    pthread_join(thread1, NULL);
    pthread_join(thread2, NULL);

    close(FileDescriptor_Socket);
    return 0;
}
