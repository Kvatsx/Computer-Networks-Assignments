// Kaustav Vats ( 2016048 )

#include <string.h>
#include <stdio.h>
#include <sys/socket.h>
// #include <sys/types.h>
#include <stdlib.h>
#include <netinet/in.h>

#define PORT 5555

int main(int argc, char const *argv[])
{
    int fd_server, n_socket;
    struct sockaddr_in server;
    int opt = 1;
    int addrlen = sizeof(server);
    char buffer[1024] = {0};
    char *Hello = "hey";

    if ((fd_server = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
		printf("Socket not created!\n");
		exit(1);
    } 

    server.sin_family = AF_INET;
	server.sin_port = htons(PORT);
    server.sin_addr.s_addr = INADDR_ANY;

    memset(server.sin_zero, '\0', sizeof server.sin_zero);

    if ( bind(fd_server, (struct sockaddr *)&server, sizeof(struct sockaddr)) < 0 ) {
        printf("Bind Fail!");
        exit(1);
    }
    listen(fd_server, 10);

    if ((n_socket = accept(fd_server, (struct sockaddr *)&server,  
                       (socklen_t*)&addrlen))<0) 
    { 
        perror("accept"); 
        exit(EXIT_FAILURE); 
    } 
    read( n_socket , buffer, 1024); 
    printf("%s\n",buffer ); 
    send(n_socket , buffer , strlen(buffer) , 0 ); 
    printf("Hello message sent\n"); 

    return 0;
}
