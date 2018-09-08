// Kaustav Vats ( 2016048 )

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/time.h>
#include "pthread.h"

#define PORT 5555
#define BUFSIZE 1024
#define connections 20

volatile int fds[connections];
volatile char Names[connections][1024];
pthread_mutex_t lock;
pthread_t tid[connections];

struct Arguments
{
    int client_socket;
    int server_socket;
};

void * ReceiveAndBroadcast(void * args) {

    struct Arguments *arg = args;

    int socket_client = arg->client_socket;
    int socket_server = arg->server_socket;

    // printf("arg-> %d\n", socket_client);
    // printf("Name: %s\n", Names[socket_client]);

    while(1) {
        // printf("arg-> %d\n", socket_client);
        // printf("Name: %s\n", Names[socket_client]);
        // int j;
        // for ( j=0; j<connections; j++ ) {
        //     if ( fds[j] != -1 ) {
        //         printf("%d ", fds[j]);
        //     }
        // }
        // printf("\n");

        char Buffer[BUFSIZE] = {0};

        /*
        recv call is used to receive mssg from a socket.
        If mssg too long then excess bytes are discarded.
        Returns -1 if socket is non blocking and no mssg is received.
        Returns 0 if socket hung up.
        */
        int MssgRecvStatus;
        if ((MssgRecvStatus = recv(socket_client, Buffer, BUFSIZE, 0)) <= 0)
        {
            if (MssgRecvStatus == 0) {
                printf("socket %d hung up\n", socket_client);
            }
            else {
                perror("recv error!\n");
            }
            pthread_mutex_lock(&lock);
            close(socket_client);
            fds[socket_client] = -1;
            memset(Names[socket_client], '\0', BUFSIZE* sizeof(char));
            pthread_mutex_unlock(&lock);

            return NULL;
        }
        else {
            // printf("MssgRecvStatus: %d\n", MssgRecvStatus);
            // Buffer[strlen(Buffer) - 1] = '\0';

            char NewBuffer[BUFSIZE];
            // printf("Mssg Recv: %s", Buffer);
            int len = sprintf(NewBuffer, "%s: %s", Names[socket_client], Buffer);
            printf("NewBuffer: %s", NewBuffer);

            int i;
            for (i = 3; i < connections; i++) {
                if (fds[i] != -1 && i != socket_server) {
                    printf("FD: %d\n", i);
                    if (send(i, NewBuffer, len, 0) == -1) {
                        perror("send error!\n");
                    }
                    else {
                        printf("Mssg sent!\n");
                    }
                }
            }
        }
    }
    return NULL;
}

int main(int argc, char const *argv[])
{
    int FileDescriptor_Socket;
    struct sockaddr_in Address;
    socklen_t AddressLength = sizeof(struct sockaddr_in);
    int OptValue = 1;
    socklen_t OptLength = sizeof(OptValue); 
    int LengthAddress = sizeof(Address);

    // fd_set Clients_FDSet;
    // int fds[20];
    int i;
    for (i = 0; i < connections; i++) {
        fds[i] = -1;
    }

    /* Creating a server File descriptor.
    IPv4
    SOCK_STREAM: TCP connection
    Protocol value: 0
    */
    if ((FileDescriptor_Socket = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("Socket not created!\n");
		exit(1);
    } 

    /* Optional 
    This method helps in reuse of address and port. 
    Prevent errors such as "address already in use."
    OptValue and OptLength are used to access option value of the method.
    OptValue should be non-zero to enable boolean option.
    */
    if ( setsockopt(FileDescriptor_Socket, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &OptValue, OptLength) ) {
        perror("setsockopt error!\n");
        exit(1);
    }

    Address.sin_family = AF_INET;
	Address.sin_port = htons(PORT);
    Address.sin_addr.s_addr = INADDR_ANY;

    memset(Address.sin_zero, '\0', sizeof Address.sin_zero);
    /*
    Forcefully attaches socket to PORT number.
    */
    if ( bind(FileDescriptor_Socket, (struct sockaddr *)&Address, sizeof(struct sockaddr)) < 0 ) {
        perror("Bind Fail!\n");
        exit(1);
    }

    /*
    Puts Server Socket in passive mode
    It waits for client to approach the server to make a connection.
    arg[1]: maxlength of the pending connections for socket queue.
    If queue is full, for new connection requests client may receive error.
    */
    if ( listen(FileDescriptor_Socket, 10) < 0 ) {
        perror("Listen error!\n");
        exit(1);
    }
    printf("Socket created...\n");
    printf("Waiting for clients on port 5555\n");

    // FD_ZERO(&Clients_FDSet);
    // FD_SET(FileDescriptor_Socket, &Clients_FDSet);
    fds[FileDescriptor_Socket] = FileDescriptor_Socket;

    if (pthread_mutex_init(&lock, NULL) != 0) {
        printf("\n Mutex init failed!\n");
        return 1;
    }
    int k=0;
    while(1) {
        int FileDescriptor_ClientSocket;
        /*
        Extracts the first connection request from the queue.
        Creates a new connected socket and returns it's file descriptor.
        */
        if ((FileDescriptor_ClientSocket = accept(FileDescriptor_Socket, (struct sockaddr *)&Address, &AddressLength)) < 0) {
            perror("Accept Fail!\n");
            exit(1);
        }
        printf("ClientSocket: %d\n", FileDescriptor_ClientSocket);
        printf("new connection from port %d \n", ntohs(Address.sin_port));
        fds[FileDescriptor_ClientSocket] = FileDescriptor_ClientSocket;
        int MssgRecvStatus;
        if ((MssgRecvStatus = recv(FileDescriptor_ClientSocket, Names[FileDescriptor_ClientSocket], BUFSIZE, 0)) <= 0) {
            if (MssgRecvStatus == 0) {
                printf("Server closed!\n");
            }
            else {
                perror("recv error!\n");
            }
            close(socket);
            break;
            exit(0);
        }
        
        printf("Name Recv: %s\n", Names[FileDescriptor_ClientSocket]);
        int length = strlen(Names[FileDescriptor_ClientSocket]);
        Names[FileDescriptor_ClientSocket][length-1] = '\0';


        struct Arguments args;
        args.client_socket = FileDescriptor_ClientSocket;
        args.server_socket = FileDescriptor_Socket;

        int error;
        pthread_t thread;
        error = pthread_create(&(tid[k]), NULL, ReceiveAndBroadcast, (void *)&args);
        if ( error != 0 ) {
            printf("Thread can't be created :[%s]\n", strerror(error));
        }
        k++;
    }

    pthread_mutex_destroy(&lock);
    close(FileDescriptor_Socket);
    return 0;
}
