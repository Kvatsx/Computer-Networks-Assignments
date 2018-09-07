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

void * SendMessage(void * arg) {

    int client_socket = (int) arg;
    char Buffer[BUFSIZE] = {0};

    printf("client_socket: %d\n", client_socket);
    while(1) {
        printf("%s ", "Enter your mssg:");
        fgets(Buffer, BUFSIZE, stdin);
        if (send(client_socket, Buffer, strlen(Buffer), 0) == -1) {
            perror("send error\n");
        }
    }
}

void * ReveiveMessage(void * arg) {
    int socket = (int) arg;
    while(1) {
        char Buffer[BUFSIZE] = {0};

        int MssgRecvStatus;
        if ((MssgRecvStatus = recv(socket, Buffer, BUFSIZE, 0)) <= 0)
        {
            if (MssgRecvStatus == 0)
            {
                printf("Server closed!\n");
            }
            else
            {
                perror("recv error!\n");
            }
            close(socket);
            break;
            exit(0);
        }
        // printf("MssgRecvStatus: %d\n", MssgRecvStatus);
        // Buffer[strlen(Buffer) - 1] = '\0';
        printf("Mssg Recv: %s\n", Buffer);
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
    // Address.sin_addr.s_addr = inet_addr("127.0.0.1");

    /*
    Converts IPv4 & IPv6 from text to Binary address.
    returns 1 on success.
    domain: AF_INET: IPv4
    src: "127.0.0.1"
    dest: &Address.sin_addr
    Conerts character string src into a network address structure,
    then copies the network address structure to dest.
    */
    if ( inet_pton(AF_INET, "127.0.0.1", &Address.sin_addr) <= 0 ) {
        perror("Invalid Address!\n");
        exit(1);
    }

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

    pthread_t thread;
    pthread_create(&thread, NULL, ReveiveMessage, (void *) FileDescriptor_Socket );
    SendMessage((void *)FileDescriptor_Socket);
    // while(1) {
        
    // }

    pthread_exit(NULL);
    close(FileDescriptor_Socket);
    return 0;
}