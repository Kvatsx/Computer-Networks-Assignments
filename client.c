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
    struct sockaddr_in client;
    int fd_socket = 0;
    struct sockaddr_in server_addr;
    char *hello = "hey";
    char buffer[1024] = {0};

    if ((fd_socket = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        printf("Socket not created\n");
		exit(1);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");

    memset(server_addr.sin_zero, '\0', sizeof server_addr.sin_zero);
    connect(fd_socket, (struct sockaddr *)&server_addr, sizeof(struct sockaddr));

    send(fd_socket, hello, strlen(hello), 0);
    printf("Hello mssg send\n");
    read(fd_socket, buffer, 1024);
    printf("%s\n", buffer);
    close(fd_socket);
    return 0;
}
